package lior.lview.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import lior.lview.LViewException;
import lior.lview.SOAPClient;
import lior.lview.jdo.JDOUtils;
import lior.lview.message.soap.FeaturesAllSaxHandler;

public class ResetCQFeaturesProcessor extends MessageProcessor
{
  @Override
  public void process(String msg) throws LViewException {
    try {
      ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
      String soapUrl = new String(MessageProcessor.readSimpleMessage(bb));

      InputStream in =
          getClass().getResourceAsStream(
              "/lior/lview/message/soap/FeaturesAll.xml");

      // Copy the SOAP file to the open connection.
      byte[] buf = new byte[2056];
      in.read(buf);
      in.close();
      MessageProcessor.CsvMessage m =
          MessageProcessor.splitHeader(new String(buf));
      FeaturesAllSaxHandler handler = new FeaturesAllSaxHandler();
      SOAPClient.send(soapUrl, m.body, handler);

      JDOUtils.persist(handler.getFeatures());
      JDOUtils.persist(handler.getRequirements());
    }
    catch (IOException e) {
      throw new LViewException(
          "errors reading soap XML file [FeaturesAll.xml]: " + e.getMessage());
    }
    catch (Exception e) {
      throw new LViewException("errors sending SOAP request: " + e.getMessage());
    }
  }

}
