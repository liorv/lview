package lior.lview.jdo;

import lior.lview.LViewException;

public class ClearByClassAction<T> extends JDOAction
{
  private Class<T> clz;

  public ClearByClassAction(Class<T> clz) {
    this.clz = clz;
  }

  @Override
  public void doIt(JDOSession session) throws LViewException{
    session.clear(clz);
  }

  @Override
  public boolean isTransactional() {
    return true;
  }

}
