package lior.lview.data;

import java.util.List;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable = "true")
public class ComponentReport implements DataObject
{
  @PrimaryKey
  String id;

  public ComponentReport(String c, String version, String build, String plat,
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

  public void process(List<TestResult> resultList) {
    int numPass = 0;
    int numFailed = 0;
    int numOther = 0;
    totalTime = 0;

    for (TestResult res : resultList) {
      if (res.getStatus().equals("PASS")) {
        numPass++;
      }
      else if (res.getStatus().equals("FAIL")) {
        numFailed++;
      }
      else {
        numOther++;
      }
      totalTime += res.getTime();
    }

    passRate = ((double) numPass) / (double) (numPass + numFailed);

    if (numOther > 1) {
      comment =
          numPass + " passed, " + numFailed + " failed, " + numOther
              + "parked/disabled";
    }
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "COMPONENT_REPORT(id=" + id + ", component=" + component
        + ", version=" + version + ", build=" + build + ", platform="
        + platform + ", db=" + db + ", passRate=" + passRate + ", totalTime="
        + totalTime + ")";
  }

  @Persistent
  String component;

  String version;

  String build;

  String platform;

  String db;

  /**
   * computed fields
   */

  double passRate;

  double totalTime;

  String comment;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getComponent() {
    return component;
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

  public double getPassRate() {
    return passRate;
  }

  public double getTotalTime() {
    return totalTime;
  }

  public String getComment() {
    return comment;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setBuild(String build) {
    this.build = build;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public void setPassRate(double passRate) {
    this.passRate = passRate;
  }

  public void setTotalTime(double totalTime) {
    this.totalTime = totalTime;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void updateFrom(ComponentReport r) {
    this.passRate = r.passRate;
    this.totalTime = r.totalTime;
  }
}
