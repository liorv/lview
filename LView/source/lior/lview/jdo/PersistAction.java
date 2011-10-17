package lior.lview.jdo;

import lior.lview.LViewException;
import lior.lview.data.DataObject;

public class PersistAction<T extends DataObject> extends JDOAction
{
  private T o;

  public PersistAction(T o) {
    this.o = o;
  }

  @Override
  public void doIt(JDOSession session) throws LViewException{
    session.persist(o);
  }

  @Override
  public boolean isTransactional() {
    return true;
  }

}
