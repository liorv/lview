package lior.lview.message;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceAware;

import lior.lview.LViewException;
import lior.lview.data.*;
import lior.lview.jdo.JDOAction;
import lior.lview.jdo.JDOSession;

@PersistenceAware
public class PingProcessor extends MessageProcessor
{
  
  private class PingAction extends JDOAction {
    
    private StringBuffer sb = new StringBuffer();
    
    private PersistenceManager pm;
    
    @Override
    public void doIt(JDOSession session) throws LViewException {
      pm = session.getPM();
      writeExtent(Pkg.class);
      writeExtent(Component.class);
      writeExtent(Feature.class);
      writeExtent(Requirement.class);
      writeExtent(Test.class);
      writeExtent(TestResult.class);
      writeExtent(ComponentReport.class);
      setResponse(sb.toString());
    }

    @Override
    public boolean isTransactional() {
      return false;
    }
    
    private void writeExtent(Class<?> clz) {
      Extent<?> all = pm.getExtent(clz);
      
      if (!all.iterator().hasNext()) return;

      sb.append("\n================================\n"
          + all.iterator().next().getClass().getName()
          + "\n================================\n");

      for (Object o : all) {
        sb.append(((DataObject) o).toString() + "\n");
      }
      sb.append("\n");
      all.closeAll();
    }
  }
  
  @Override
  public void process(String msg) throws LViewException {
    (new PingAction()).perform();
  }
}
