package lior.lview.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import lior.lview.LViewException;
import lior.lview.jdo.JDOAction;
import lior.lview.jdo.JDOSession;

public class ComponentResultsProc
{
  private String compName;
  
  public ComponentResultsProc(String compName) {
    this.compName = compName;
  }

  /**
   * Persistence layer for this class abstracted in JDOAction object
   * 
   * @author liorv
   */
  private class MyPersistAction extends JDOAction
  {
    private String compName;
    private List<ComponentReport> ctxList;

    MyPersistAction(String compName, List<ComponentReport> ctxList) {
      super();
      this.compName = compName;
      this.ctxList = ctxList;
    }

    @Override
    public void doIt(JDOSession session) throws LViewException {
      Component component = session.find(Component.class, compName);
      if(component == null) {
        throw new LViewException("rejecting test results from unknown component ["+compName+"]");
      }
      component.setCurrentBuild(ctxList.get(0).getBuild());
      component.setCurrentRelease(ctxList.get(0).getVersion());
      session.persist(component);
      
      Vector<ComponentReport> retval = new Vector<ComponentReport>();
      for (ComponentReport r : ctxList) {
        ComponentReport found = session.find(ComponentReport.class, r.getId());
        if(found != null) {
          found.updateFrom(r);
          r = found;
        }    
        retval.add(r);
      }
      session.persist(retval);      
    }

    @Override
    public boolean isTransactional() {
      return true;
    }

  }

  public void process(Collection<TestResult> results) throws LViewException {
    if (results.size() == 0) return;

    List<ComponentReport> ctxList = summarizeComponent(results);
    (new MyPersistAction(compName, ctxList)).perform();
  }

  private String context(TestResult res) {
    String dbStr = (res.getDb() == null) ? "" : ("/" + res.getDb());
    return (res.getBuild() + "__" + res.getPlatform() + dbStr);
  }

  private HashSet<String> getContextSet(Collection<TestResult> testResults) {
    HashSet<String> contextSet = new HashSet<String>();
    String lastContext = "";
    for (TestResult res : testResults) {
      String curContext = context(res);
      if (!curContext.equals(lastContext)) {
        contextSet.add(curContext);
        lastContext = curContext;
      }
    }
    return contextSet;
  }

  private List<ComponentReport> summarizeComponent(
      Collection<TestResult> testResults) throws LViewException
  {
    HashSet<String> contextSet = getContextSet(testResults);
    List<ComponentReport> retval = new Vector<ComponentReport>();

    for (String ctx : contextSet) {
      retval.add(summarizeComponent(testResults, ctx));
    }

    return retval;
  }

  private ComponentReport summarizeComponent(
      Collection<TestResult> testResults, String ctx)
  {
    Vector<TestResult> resVec = new Vector<TestResult>();

    for (TestResult res : testResults) {
      if (ctx.equals(context(res))) {
        resVec.add(res);
      }
    }

    TestResult r = resVec.firstElement();
    ComponentReport cr =
        new ComponentReport(compName, r.getVersion(), r.getBuild(),
            r.getPlatform(), r.getDb());

    cr.process(resVec);
    return cr;
  }
}
