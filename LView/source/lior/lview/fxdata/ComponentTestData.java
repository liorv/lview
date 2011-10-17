package lior.lview.fxdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;
import javax.jdo.annotations.PersistenceAware;

import org.apache.log4j.Logger;

import lior.lview.LViewException;
import lior.lview.data.TestResult;
import lior.lview.jdo.JDOAction;
import lior.lview.jdo.JDOSession;

@PersistenceAware
public class ComponentTestData
{
  private static Logger log = Logger.getLogger(ComponentTestData.class);

  public static final int MAX_BUILDS = 3;
  public String component;

  public String[] builds;
  public String[] platforms;
  
  public Collection<TestResult> testResults;

  private class GetTestResultsAction extends JDOAction
  {
    @SuppressWarnings("unchecked")
    @Override
    public void doIt(JDOSession session) throws LViewException {
      List<String> buildList = Arrays.asList(builds);
      List<String> platList = Arrays.asList(platforms);
      
      Map<String, Object> kvMap = new HashMap<String, Object>();
      kvMap.put("build", buildList);
      
      if(platList != null && platList.size() > 0) {
        kvMap.put("platform", platList);
      }
      Query q = session.makeQuery(TestResult.class, kvMap);
      //testResults = (Collection<TestResult>) q.execute();
      testResults = session.getPM().detachCopyAll( (Collection<TestResult>) q.execute() );
    }

    @Override
    public boolean isTransactional() {
      return false;
    }

  }

  public ComponentTestData(String component, String[] buildArr, String[] platArr) {
    for(int i=0; i<buildArr.length; i++) {
      log.info(i+"> "+buildArr[i]);
    }
    
    this.component = component;
    this.builds = buildArr;
    this.platforms = platArr;
    
    try {
      (new GetTestResultsAction()).perform();
    }
    catch (LViewException lv) {
      log.error(lv.getMessage());
    }
  }

}
