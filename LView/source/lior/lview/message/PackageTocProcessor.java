package lior.lview.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import lior.lview.LViewException;
import lior.lview.data.Pkg;
import lior.lview.jdo.JDOUtils;

public class PackageTocProcessor extends MessageProcessor
{
  enum Meta {
    release, packageNumber, platform, db
  }

  class PackageMeta
  {
    public String release;
    public String packageNumber;
    public String platform;
    public String db;
  }

  @Override
  public void process(String msg) throws LViewException {
    try {
      BufferedReader br = new BufferedReader(new StringReader(msg));
      String packageHeaderStr = br.readLine();
      String packageInfoStr = br.readLine();
      String[] packageHeader = packageHeaderStr.split(",", -1);
      String[] packageInfo = packageInfoStr.split(",", -1);

      PackageMeta pm = getPackageMeta(packageHeader, packageInfo);
      String metaHeaderStr = br.readLine();
      String[] metaHeader = metaHeaderStr.split(",", -1);
      if (metaHeader.length != 3)
        throw new LViewException("package TOC component header has ["
            + metaHeaderStr + "] elements, expecting 3 elements");
      
      Pkg p = new Pkg(pm.release, pm.packageNumber, pm.platform, pm.db);
      
      for (String compStr = br.readLine(); compStr != null; compStr =
          br.readLine()) {
        String[] c = compStr.split(",", -1);
        p.addComponent(c[0], c[1], c[2]);
      }
      
      JDOUtils.persist(p);
    }
    catch (IOException e) {
      throw new LViewException(
          "failed to read meta-data from package TOC message. reason=["
              + e.getMessage() + "]");
    }
  }

  private PackageMeta getPackageMeta(String[] metaHeader, String[] metaData)
      throws LViewException
  {
    HashMap<String, String> metaMap = new HashMap<String, String>();
    for (int i = 0; i < metaHeader.length; i++) {
      metaMap.put(metaHeader[i], metaData[i]);
    }
    PackageMeta meta = new PackageMeta();
    meta.packageNumber = metaMap.get(Meta.packageNumber.toString());
    meta.release = metaMap.get(Meta.release.toString());
    meta.platform = metaMap.get(Meta.platform.toString());
    meta.db = metaMap.get(Meta.db.toString());
    return meta;
  }

}
