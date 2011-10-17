package lior.lview.data;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
public class Component implements DataObject
{
  @PrimaryKey
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  private String currentRelease;

  private String currentBuild;

  private String info;

  public Component(String name) {
    setId(name);
    this.currentRelease = "0.0.0";
    this.currentBuild = "0000";
    this.info = "<DESCRIPTION>";
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "COMPONENT{id=" + id + ", release=" + currentRelease + ", "
        + "build=" + currentBuild + ", info=" + info +"}";
  }

  public String getCurrentRelease() {
    return currentRelease;
  }

  public String getCurrentBuild() {
    return currentBuild;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public void setCurrentRelease(String r) {
    if(r == null)
      return;
    
    if (currentRelease.compareTo(r) < 0) {
      this.currentRelease = r;
    }
  }

  public void setCurrentBuild(String b) {
    if(b == null)
      return;
    
    if (currentBuild.compareTo(b) < 0) {
      this.currentBuild = b;
    }
  }

}
