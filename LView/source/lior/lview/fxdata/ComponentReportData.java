package lior.lview.fxdata;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import lior.lview.data.Component;
import lior.lview.data.ComponentReport;

public class ComponentReportData
{
  private static class ReverseComparator implements Comparator<String>
  {

    @Override
    public int compare(String o1, String o2) {
      return o2.compareTo(o1);
    }
  }
  
  private static ReverseComparator rc = new ReverseComparator();

  public String component;
  
  public Component componentInfo;
  
  public TreeSet<String> versions;

  // these depend on the version
  public HashMap<String, TreeSet<String>> ver2platforms;
  public HashMap<String, TreeSet<String>> ver2builds;

  public ComponentReportData(String cname, Component c) {
    component = cname;
    componentInfo = c;
    versions = new TreeSet<String>(rc);

    ver2platforms = new HashMap<String, TreeSet<String>>();
    ver2builds = new HashMap<String, TreeSet<String>>();
  }

  public void addReport(ComponentReport cr) {
    versions.add(cr.getVersion());
    
    addToVecMap(ver2builds, cr.getVersion(), cr.getBuild(), rc);
    addToVecMap(ver2platforms, cr.getVersion(), cr.getPlatform(), null);    
  }

  private void addToVecMap(Map<String, TreeSet<String>> map, String key,
      String value, Comparator<String> c)
  {
    TreeSet<String> vec = map.get(key);
    if (vec == null) {
      vec = (c == null) ? new TreeSet<String>() : new TreeSet<String>(c);
      map.put(key, vec);
    }
    vec.add(value);
  }
}
