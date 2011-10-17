package lior.lview.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.*;

import lior.lview.jdo.JDOUtils;

@PersistenceCapable(detachable="true")
public class Requirement implements DataObject
{
  @PrimaryKey
  private String id;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  private String featureId;

  private String info;

  private String component;

  private Set<String> testIds;

  public Requirement(String id, String featureId, String component,
      String info, Set<String> tests)
  {
    setId(id);
    this.featureId = featureId;
    this.info = info;
    this.component = component;
    this.testIds = new HashSet<String>(tests);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public void addTest(Test t) {
    testIds.add(t.getId());
  }

  @Override
  public String toString() {
    return "REQUIREMENT{id=" + id + ", feature=" + featureId + ", info=" + info
        + ", component=" + component + ", tests=" + testIds + "}";
  }

  public String getInfo() {
    return info;
  }
  
  public String getComponent() {
    return component;
  }

  public Set<String> getTestIds() {
    return testIds;
  }
  
  public Collection<Test> getTests() {
    return JDOUtils.findByIds(Test.class, testIds);
  }

}
