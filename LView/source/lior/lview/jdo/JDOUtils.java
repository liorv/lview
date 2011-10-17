package lior.lview.jdo;

import java.util.Collection;
import java.util.Set;

import lior.lview.LViewException;
import lior.lview.data.DataObject;

public class JDOUtils
{
  public static <T> T find(Class<T> clz, String id) throws LViewException {
    JDOSession session = JDOSession.open();   
    T retval = session.find(clz, id);
    retval = session.getPM().detachCopy(retval);
    session.close();
    
    return retval;
  }

  public static <T extends DataObject> Collection<T> findByIds(Class<T> clz,
      Set<String> oids)
  {
    JDOSession session = JDOSession.open();
    Collection<T> retval = session.findByIds(clz, oids);
    retval = session.getPM().detachCopy(retval);
    session.close();
    
    return retval;
  }

  public static <T extends DataObject> void persist(T o) throws LViewException {
    (new PersistAction<T>(o)).perform();
  }
  
  public static <T extends DataObject> void persist(Collection<T> coll) throws LViewException {
    (new PersistManyAction<T>(coll)).perform();
  }

}
