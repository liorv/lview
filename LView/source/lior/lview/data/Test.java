package lior.lview.data;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
public class Test implements DataObject
{
  @PrimaryKey
  private String id;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  private String component;

  private String bucket;

  private String test;

  private String info;

  public Test(String component, String bucket, String test, String info) {
    setId(createKey(component, bucket, test));
    this.component = component;
    this.bucket = bucket;
    this.test = test;
    this.info = info;
  }

  public static String createKey(String component, String bucket, String test)
  {
    String retval =
        Test.class.getSimpleName() + "(" + component + "," + bucket + ","
            + test + ")";
    return retval;
  }

  @Override
  public String toString() {
    return "TEST{component=" + component + ", bucket=" + bucket + ", test="
        + test + ", info=" + info + "}";
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public String getComponent() {
    return component;
  }

  public String getBucket() {
    return bucket;
  }

  public String getTest() {
    return test;
  }

  public String getInfo() {
    return info;
  }
}
