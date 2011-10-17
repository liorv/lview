package lior.lview.message;

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.log4j.Logger;

import lior.lview.ConnectionHandler;
import lior.lview.LViewException;

public class BulkLoadProcessor extends MessageProcessor
{
  private static Logger log = Logger.getLogger(MessageProcessor.class);
  
  enum Meta {
    name, info
  }

  @Override
  public void process(String msg, DataInputStream dis) throws LViewException {
    int i = -1;
    try {
      ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
      int numMessages = bb.getInt();
      log.info("trying to bulk-load ["+numMessages+"] messages=");
      Vector<byte[]> msgVector =
          MessageProcessor.readSimpleMessages(dis, numMessages);
      
      for (i = 0; i < msgVector.size(); i++) {
        try {
          ConnectionHandler.processMessage(msgVector.elementAt(i), dis);
        }
        catch(LViewException e) {
          throw e;
        }
        catch (Exception e) {
          e.printStackTrace();
          response("Skipping! -- Unable to process record idx [" + i + "]:\n\treason=["
              + e.getMessage() + "]\n");
        }        
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LViewException("failed to read bulk messages idx=[" + i
          + "].\n\treason=[" + e.getMessage() + "]");
    }
  }

  @Override
  public void process(String msg) throws LViewException {
    throw new Error("BUG: why am I here?");
  }
}
