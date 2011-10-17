package lior.lview.jdo;

import lior.lview.LViewException;
import lior.lview.data.PersistedClasses;

public class ClearAllAction extends JDOAction
{
  public ClearAllAction() {}

  @Override
  public void doIt(JDOSession session) throws LViewException {
    for (Class<?> clz : PersistedClasses.list()) {
      try {
        session.clear(clz);
      }
      catch (Exception e) {
      }
    }
  }

  @Override
  public boolean isTransactional() {
    return true;
  }

}
