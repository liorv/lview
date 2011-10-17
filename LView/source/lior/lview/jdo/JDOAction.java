package lior.lview.jdo;

import org.apache.log4j.Logger;

import lior.lview.LViewException;

abstract public class JDOAction
{
  private static Logger log = Logger.getLogger(JDOAction.class);
  
  protected JDOAction() {}

  public abstract void doIt(JDOSession session) throws LViewException;

  public abstract boolean isTransactional();

  public void perform() throws LViewException {
    JDOSession session = null;

    if(log.isDebugEnabled())
      log.debug("perform(" + this.getClass().getSimpleName() + ")");
    try {
      session = JDOSession.open(isTransactional());
      doIt(session);
      session.close();
    }
    catch (LViewException lv) {
      lv.printStackTrace();
      throw lv;
    }
    finally {
      session.rollback();
    }
  }
}
