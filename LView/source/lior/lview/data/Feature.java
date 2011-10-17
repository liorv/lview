package lior.lview.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.*;

import lior.lview.jdo.JDOUtils;

@PersistenceCapable(detachable="true")
public class Feature implements DataObject
{
  @PrimaryKey
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String name;

  private Set<String> requirementIds;

  public Feature(String id, String name, Set<String> requirements) {
    setId(id);
    this.name = name;
    this.requirementIds = new HashSet<String>(requirements);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public void addRequirement(Requirement r) {
    requirementIds.add(r.getId());
  }

  @Override
  public String toString() {
    return "FEATURE{id=" + id + ", name=" + name + ", requirements="
        + requirementIds + "}";
  }

  public String getName() {
    return name;
  }

  public Set<String> getRequirementIds() {
    return requirementIds;
  }

  public Collection<Requirement> getRequirements() {
    return JDOUtils.findByIds(Requirement.class, requirementIds);
  }

}
