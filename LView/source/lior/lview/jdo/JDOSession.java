package lior.lview.jdo;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.annotations.PersistenceAware;

import org.apache.log4j.Logger;

import lior.lview.LViewException;
import lior.lview.data.DataObject;

@PersistenceAware
public final class JDOSession
{
  private static final boolean SAFE_MODE = true;
  
  private static final boolean LOG_FIND_ERRORS = false;

  private static Logger log = Logger.getLogger(JDOSession.class);

  private static final PersistenceManagerFactory pmfInstance = JDOHelper
      .getPersistenceManagerFactory("dn.props");

  private PersistenceManager pm;
  private Transaction tx;
  private boolean i_transactional;

  private JDOSession() {
    pm = pmfInstance.getPersistenceManager();
  }

  public PersistenceManager getPM() {
    return pm;
  }

  public static JDOSession open() {
    return open(false);
  }

  public static JDOSession open(boolean transactional) {
    JDOSession retval = new JDOSession();
    retval.pm = retval.getPM();
    retval.i_transactional = transactional;
    retval.pm.currentTransaction().begin();

    return retval;
  }

  public void rollback() {
    if (!pm.isClosed()) {
      tx = pm.currentTransaction();
      if (i_transactional && tx.isActive()) tx.rollback();
      pm.close();
    }
  }

  public void close() {
    try {
      tx = pm.currentTransaction();
      if (tx.isActive()) tx.commit();
      pm.close();
    }
    catch (javax.jdo.JDODataStoreException dse) {
      System.err.println("Exception Caught JDODataStoreException: [" + dse.getMessage() + "]");
      Throwable[] tArr = dse.getNestedExceptions();
      for (Throwable t : tArr) {
        if (t instanceof SQLException)
          printNested((SQLException) t);
        else
          System.err.println("\treason: [" + t.getMessage() + "]");
      }
    }
  }

  private static void printNested(SQLException t) {
    System.err.println("\treason: [" + t.getMessage() + "]");
    SQLException next = t.getNextException();
    if (next != null) printNested(next);
  }

  public <T> Query makeQuery(Class<T> clz, Map<String, Object> keyValueMap) {
    try {
      StringBuffer sb = new StringBuffer();
      Iterator<Entry<String, Object>> iter = keyValueMap.entrySet().iterator();

      while (iter.hasNext()) {
        Entry<String, Object> e = iter.next();
        if (e.getValue() instanceof Collection<?>) {
          Collection<?> coll = (Collection<?>) e.getValue();
          sb.append("(");
          for (Iterator<?> it = coll.iterator(); it.hasNext();) {
            sb.append(e.getKey() + " == "
                + fieldValue(clz, e.getKey(), it.next().toString()));
            if (it.hasNext()) sb.append(" || ");
          }
          sb.append(")");
        }
        else if (e.getValue() instanceof String) {
          sb.append(e.getKey() + " == "
              + fieldValue(clz, e.getKey(), (String) e.getValue()));
        }
        else {
          throw new Error("BUG: can't make query with parameter "
              + e.getValue());
        }
        if (iter.hasNext()) sb.append(" && ");
      }
      log.info("QUERY == "+sb.toString());
      return pm.newQuery(clz, sb.toString());
    }
    catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unused")
  public <T> T find(Class<T> clz, String id) {
    try {
      return pm.getObjectById(clz, id);
    }
    catch (Exception e) {
      if (LOG_FIND_ERRORS && log.isDebugEnabled()) {
        log.debug("cannot find a " + clz.getSimpleName() + " with id=[" + id
            + "]\n\treason: [" + e.getMessage() + "]");
      }
    }
    return null;
  }

  private static <T> String fieldValue(Class<T> clz, String k, String v) {
    try {
      Field f = clz.getDeclaredField(k);
      Class<?> type = f.getType();
      if (String.class.equals(type)) return "\"" + v + "\"";
    }
    catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    return v;
  }

  public <T extends DataObject> Collection<T> findByIds(Class<T> clz,
      Set<String> oids)
  {
    Vector<T> retval = new Vector<T>();
    for (String id : oids) {
      T obj = find(clz, id);
      if (obj != null) {
        retval.add(obj);
      }
    }

    return retval;
  }

  public <T extends DataObject> void persist(T o) throws LViewException {
    try {
      pm.makePersistent(o);
    }
    catch (Exception e) {
      log.warn("couldn't persist single [" + o + "]: \n\treason: [" + e.getMessage()
          + "]\n");
    }
  }

  public <T extends DataObject> void persist(Collection<T> coll)
      throws LViewException
  {
    if (SAFE_MODE)
      persist_safe(coll);
    else
      persist_fast(coll);
  }

  private <T extends DataObject> void persist_safe(Collection<T> coll)
      throws LViewException
  {
    StringBuffer sb = new StringBuffer();

    T temp = null;
    for (Iterator<T> iter = coll.iterator(); iter.hasNext();) {
      try {
        temp = iter.next();
        pm.makePersistent(temp);
      }
      catch (Exception e) {
        sb.append("couldn't persist [" + temp.getId() + "]: \n\treason: " + e.getMessage() + "\n");
      }
    }
    if (sb.length() > 0) log.warn(sb.toString());
  }

  private <T extends DataObject> void persist_fast(Collection<T> coll)
      throws LViewException
  {
    String reason = null;
    try {
      pm.makePersistentAll(coll);
    }
    catch (Exception e) {
      reason = e.getMessage();
    }
    if (reason != null) {
      String className = coll.iterator().next().getClass().getSimpleName();
      throw new LViewException("Unable to persist collection [" + className
          + "] object. Reason=[" + reason + "]");
    }
  }

  public <T> void clear(Class<T> clz) throws LViewException {
    String reason = null;
    try {
      Query q = pm.newQuery(clz);
      q.deletePersistentAll();
    }
    catch (Exception e) {
      e.printStackTrace();
      reason = e.getMessage();
    }
    if (reason != null) {
      log.warn("Unable to clear [" + clz.getSimpleName() + "] object. Reason=["
          + reason + "]");
    }
  }

  private <T extends DataObject> void clear_byIdList_deletePersistentAll(
      Class<T> clz, Collection<String> oids) throws LViewException
  {
    String reason = null;
    try {
      Vector<T> removeList = new Vector<T>();
      for (Iterator<String> iter = oids.iterator(); iter.hasNext();) {
        T o = pm.getObjectById(clz, iter.next());
        removeList.add(o);
      }
      pm.deletePersistentAll(removeList);
    }
    catch (Exception e) {
      reason = e.getMessage();
    }
    if (reason != null)
      log.warn("Unable to clear [" + clz.getSimpleName() + "] object. Reason=["
          + reason + "]");
  }

  public <T extends DataObject> void clear(Class<T> clz, Collection<String> oids)
      throws LViewException
  {
    if (SAFE_MODE)
      clear_byIdList_deleteOneByOne(clz, oids);
    else
      clear_byIdList_deletePersistentAll(clz, oids);
  }

  private <T extends DataObject> void clear_byIdList_deleteOneByOne(
      Class<T> clz, Collection<String> oids) throws LViewException
  {
    String reason = null;
    try {
      for (Iterator<String> iter = oids.iterator(); iter.hasNext();) {
        try {
          T obj = (T) pm.getObjectById(clz, iter.next());
          pm.deletePersistent(obj); // object is marked for deletion
        }
        catch (Exception e) {
        }
      }
    }
    catch (Exception e) {
      reason = e.getMessage();
    }
    if (reason != null)
      log.warn("Unable to clear [" + clz.getSimpleName() + "] object. Reason=["
          + reason + "]");
  }

}