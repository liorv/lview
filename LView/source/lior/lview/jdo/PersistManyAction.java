package lior.lview.jdo;

import java.util.Collection;

import lior.lview.LViewException;
import lior.lview.data.DataObject;

public class PersistManyAction<T extends DataObject> extends JDOAction
{
  private Collection<T> coll;

  public PersistManyAction(Collection<T> coll) {
    this.coll = coll;
  }

  @Override
  public void doIt(JDOSession session) throws LViewException {
    session.persist(coll);
  }

  @Override
  public boolean isTransactional() {
    return true;
  }

}
