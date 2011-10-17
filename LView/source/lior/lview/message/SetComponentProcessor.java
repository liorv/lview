package lior.lview.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.jdo.annotations.PersistenceAware;

import lior.lview.LViewException;
import lior.lview.data.Component;
import lior.lview.jdo.JDOUtils;

@PersistenceAware
public class SetComponentProcessor extends MessageProcessor
{  
  enum Meta {
    name, info
  }

  @Override
  public void process(String msg) throws LViewException {
    try {
      BufferedReader br = new BufferedReader(new StringReader(msg));
      String componentInfoHeader = br.readLine();
      String componentInfoData = br.readLine();
      String[] header = componentInfoHeader.split(",", -1);
      String[] data = componentInfoData.split(",", -1);

      HashMap<String, String> metaMap = new HashMap<String, String>();
      for (int i = 0; i < header.length; i++) {
        metaMap.put(header[i], data[i]);
      }

      Component c = new Component(metaMap.get(Meta.name.toString()));
      Component found = JDOUtils.find(Component.class, c.getId());
      if(found != null)
        c = found;
      
      c.setInfo(metaMap.get(Meta.info.toString()));

      JDOUtils.persist(c);
    }
    catch (IOException e) {
      throw new LViewException(
          "failed to set component. reason=["
              + e.getMessage() + "]");
    }
  }

}
