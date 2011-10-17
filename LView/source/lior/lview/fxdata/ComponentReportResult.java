package lior.lview.fxdata;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import javax.jdo.Extent;
import javax.jdo.annotations.PersistenceAware;

import org.apache.log4j.Logger;

import lior.lview.LViewException;
import lior.lview.data.Component;
import lior.lview.data.ComponentReport;
import lior.lview.jdo.JDOAction;
import lior.lview.jdo.JDOSession;

@PersistenceAware
public class ComponentReportResult
{
  private static Logger log = Logger.getLogger(ComponentReportResult.class);

  public Collection<String> fields = new TreeSet<String>();

  public Collection<String> components = new TreeSet<String>();

  public Map<String, ComponentReportData> c2data =
      new HashMap<String, ComponentReportData>();

  public Collection<ComponentReport> reports =
      new LinkedList<ComponentReport>();

  private class GetReportsAction extends JDOAction
  {
    @Override
    public void doIt(JDOSession session) throws LViewException {
      Extent<ComponentReport> extentCRs =
          session.getPM().getExtent(ComponentReport.class);

      for (ComponentReport cr : extentCRs) {
        String cname = cr.getComponent();
        components.add(cname);
        ComponentReportData cdata = c2data.get(cname);
        if (cdata == null) {
          Component cobj = session.getPM().getObjectById(Component.class, cname);
          cdata = new ComponentReportData(cname, cobj);
          c2data.put(cname, cdata);
        }
        cdata.addReport(cr);
        reports.add(cr);
      }
      extentCRs.closeAll();
    }

    @Override
    public boolean isTransactional() {
      return false;
    }

  }

  public ComponentReportResult() {
    String[] fieldArr = { "component", "platform", "version", "build" };
    fields = Arrays.asList(fieldArr);

    try {
      (new GetReportsAction()).perform();
    }
    catch (LViewException lv) {
      log.error(lv.getMessage());
    }
  }
}
