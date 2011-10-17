package lior.lview.data;

import java.util.List;
import java.util.Vector;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
public class Pkg implements DataObject
{
  @PrimaryKey
  private String id;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  private String release;
  
  private String packageNumber;

  private String platform;

  private String db;

  private List<String> componentNames = new Vector<String>();

  private List<String> componentVersions = new Vector<String>();

  private List<String> componentBuilds = new Vector<String>();

  public Pkg(String release, String packageNum, String platform, String db)
  {
    setId(createKey(release, packageNum, platform, db));
    this.packageNumber = packageNum;
    this.platform = platform;
    this.db = db;
  }

  public void addComponent(String component, String version, String build) {
    componentNames.add(component);
    componentVersions.add(version);
    componentBuilds.add(build);
  }

  public static String createKey(String release, String packageNum,
      String platform, String db)
  {
    String retval =
        Pkg.class.getSimpleName() + "(" + release + "," + packageNum + ","
            + platform + "," + db +")";
    return retval;
  }

  @Override
  public int hashCode() {
    return packageNumber.hashCode();
  }

  public String getRelease() {
    return release;
  }

  public String getPackageNumber() {
    return packageNumber;
  }

  public String getPlatform() {
    return platform;
  }

  public String getDb() {
    return db;
  }

  public List<String> getComponentNames() {
    return componentNames;
  }

  public List<String> getComponentVersions() {
    return componentVersions;
  }

  public List<String> getComponentBuilds() {
    return componentBuilds;
  }
}
