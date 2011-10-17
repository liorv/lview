package lior.lview.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import lior.lview.LViewException;
import lior.lview.data.ComponentResultsProc;
import lior.lview.data.Test;
import lior.lview.data.TestResult;
import lior.lview.jdo.JDOAction;
import lior.lview.jdo.JDOSession;

public class DailyTestResultsProcessor extends MessageProcessor
{
  private static Logger log = Logger.getLogger(MessageProcessor.class);

  class ReportMeta
  {
    public String component;
    public String version;
    public String build;
    public String platform;
    public String db;
  }

  enum Status {
    PASS, FAIL, PARKED, OTHER
  }

  enum Meta {
    component, version, build, plat, db
  }

  enum TestHeader {
    bucket, test, status, time, reason
  }

  @Override
  public void process(String msg) throws LViewException {
    String metaDataStr = "DATA_HEADER=?";
    try {
      BufferedReader br = new BufferedReader(new StringReader(msg));
      String metaHeaderStr = br.readLine();
      metaDataStr = br.readLine();
      String[] metaHeader = metaHeaderStr.split(",", -1);
      String[] metaData = metaDataStr.split(",", -1);

      ReportMeta meta = getReportMeta(metaHeader, metaData);

      Vector<TestResult> results = getTestResults(meta, br);

      processTestResults(meta.component, results);
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LViewException("failed to load daily report [" + metaDataStr
          + "]. \n\treason=[" + e.getMessage() + "]");
    }
  }

  public void processTestResults(String compName, Collection<TestResult> results)
      throws LViewException
  {
    ComponentResultsProc rProc = new ComponentResultsProc(compName);
    rProc.process(results);
  }

  private ReportMeta getReportMeta(String[] metaHeader, String[] metaData)
      throws LViewException
  {
    HashMap<String, String> metaMap = new HashMap<String, String>();
    for (int i = 0; i < metaHeader.length; i++) {
      metaMap.put(metaHeader[i], metaData[i]);
    }
    ReportMeta meta = new ReportMeta();
    meta.component = metaMap.get(Meta.component.toString());
    meta.version = metaMap.get(Meta.version.toString());
    meta.build = metaMap.get(Meta.build.toString());
    meta.platform = metaMap.get(Meta.plat.toString());
    meta.db = metaMap.get(Meta.db.toString());
    return meta;
  }

  private Vector<TestResult> getTestResults(ReportMeta meta, BufferedReader br)
      throws IOException, LViewException
  {
    Vector<TestResult> retval = new Vector<TestResult>();

    String[] reportHeader = br.readLine().split(",", -1);
    if (reportHeader.length < 5)
      throw new LViewException("report header must be contain at least 5 cols");

    int[] indices = { -1, -1, -1, -1, -1 };
    for (int i = 0; i < reportHeader.length; i++) {
      TestHeader tm = TestHeader.valueOf(reportHeader[i]);
      switch (tm) {
        case bucket:
        case test:
        case status:
        case time:
        case reason:
          indices[tm.ordinal()] = i;
          break;
        default:
      }
    }
    for (int idx : indices)
      if (idx == -1)
        throw new LViewException("missing required report column");

    for (String testStr = br.readLine(); testStr != null; testStr =
        br.readLine()) {
      try {
        String[] testFields = testStr.split(",", -1);
        String version = meta.version;
        String bucket = testFields[indices[TestHeader.bucket.ordinal()]];
        String test = testFields[indices[TestHeader.test.ordinal()]];
        String build = meta.build;
        String statusStr = testFields[indices[TestHeader.status.ordinal()]];
        String time = testFields[indices[TestHeader.time.ordinal()]];
        String reason = testFields[indices[TestHeader.reason.ordinal()]];
        String testId = Test.createKey(meta.component, bucket, test);
        TestResult r =
            new TestResult(testId, version, build, meta.platform, meta.db,
                statusStr, Long.parseLong(time), reason);

        retval.add(r);
      }
      catch (Exception e) {
        log.warn("Skipping! test [" + testStr + "] because: \n\t"
            + e.getMessage());
      }
    }
    UpdateTestResultsAction act = new UpdateTestResultsAction(retval);
    act.perform();
    return act.getTestResults();
  }

  /**
   * Persistence layer for this class abstracted in JDOAction object
   * 
   * @author liorv
   */
  private class UpdateTestResultsAction extends JDOAction
  {
    private Vector<TestResult> testResults;

    public UpdateTestResultsAction(Vector<TestResult> results) {
      this.testResults = results;
    }
    
    public Vector<TestResult> getTestResults() {
      return testResults;
    }

    @Override
    public void doIt(JDOSession session) throws LViewException {
      Vector<TestResult> retval = new Vector<TestResult>();
      
      for (TestResult r : testResults) {
        TestResult found = session.find(TestResult.class, r.getId());
        if(found != null) {
          found.updateFrom(r);
          r = found;
        }    
        retval.add(r);
      }
      
      testResults = retval;
      session.persist(retval);
    }

    @Override
    public boolean isTransactional() {
      return true;
    }

  }

}
