package lior.lview.data;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable = "true")
public class TestResult implements DataObject
{
  @PrimaryKey
  String id;

  public void updateFrom(TestResult r) {
    this.status = r.status;
    this.time = r.time;
    this.reason = r.reason;
  }
  
  public TestResult(String c, String version, String build, String plat,
      String db)
  {
    setId(createKey(c, version, build, plat, db));
    this.component = c;
    this.version = version;
    this.build = build;
    this.platform = plat;
    this.db = db;
  }

  public static String createKey(String c, String version, String build,
      String plat, String db)
  {
    String retval =
        ComponentReport.class.getSimpleName() + "(" + c + "," + version + ","
            + build + "," + plat + ")";
    if (db != null && db.length() > 0) {
      retval += ("::" + db);
    }
    return retval;
  }

  

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Persistent
  String component;

  String version;

  String build;

  String platform;

  String db;

  String status;

  long time;

  String reason;

  public TestResult(String test_id, String version, String build, String plat,
      String db, String status, long time, String reason)
  {
    setId(createKey(test_id, version, build, plat, db));
    this.id = test_id;
    this.version = version;
    this.build = build;
    this.platform = plat;
    this.db = db;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBuild() {
    return build;
  }

  public void setBuild(String build) {
    this.build = build;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

 
}


/*package lior.lview.data;

import javax.jdo.annotations.*;


{
  

  public static String createKey(String test_id, String version, String build,
      String plat, String db)
  {
    String retval =
        TestResult.class.getSimpleName() + "(" + test_id + "," + version + ","
            + build + "," + plat + "," + db + ")";
    if (db != null && db.length() > 0) {
      retval += ("::" + db);
    }
    return retval;
  }

  @Override
  public String toString() {
    return getKey() + " -> status(" + getStatus() + "), time(" + getTime()
        + "), reason(" + getReason() + ")";
  }

  public TestResult(String test_id, String version, String build, String plat,
      String db, String status, long time, String reason)
  {
    setId(createKey(test_id, version, build, plat, db));
    this.id = test_id;
    this.version = version;
    this.build = build;
    this.platform = plat;
    this.db = db;
    this.status = status;
    this.time = time;
    this.reason = reason;
  }

  String version;

  String build;

  String platform;

  String db;

  String status;

  long time;

  String reason;

  public String getKey() {
    return id;
  }

  public String getTestId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public String getBuild() {
    return build;
  }

  public String getPlatform() {
    return platform;
  }

  public String getDb() {
    return db;
  }

  public String getStatus() {
    return status;
  }

  public long getTime() {
    return time;
  }

  public String getReason() {
    return reason;
  }

  public void updateFrom(TestResult r) {
    this.status = r.status;
    this.time = r.time;
    this.reason = r.reason;
  }

}
*/