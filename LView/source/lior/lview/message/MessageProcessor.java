package lior.lview.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.log4j.Logger;

import lior.lview.LViewException;

abstract public class MessageProcessor
{
  private static Logger log = Logger.getLogger(MessageProcessor.class);
  
  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  private String response;
  
  protected void response(String msg) {
    if(response == null)
      response = msg;
    else
      response += msg;
  }

  public static class CsvMessage
  {
    public CsvMessage(String h, String b) {
      header = h;
      body = b;
    }

    public String header;
    public String body;
  }

  abstract public void process(String msg) throws LViewException;
  
  public void process(String msg, DataInputStream dis) throws LViewException {
    process(msg);
  }

  public static byte[] readSimpleMessage(DataInputStream dis)
      throws IOException
  {
    int msgSize = dis.readInt();    
    
    log.debug("[<] getting message of size [" + msgSize + "]");
    byte[] msg = new byte[msgSize];
    dis.readFully(msg);

    return msg;
  }

  public static byte[] readSimpleMessage(ByteBuffer bb) throws IOException {
    int msgSize = bb.getInt();
    log.debug("[<] getting message of size [" + msgSize + "]");
    byte[] msg = new byte[msgSize];
    bb.get(msg);
    return msg;
  }
  
  public static Vector<byte[]> readSimpleMessages(DataInputStream dis, int numMessages) throws IOException {    
    Vector<byte[]> retval = new Vector<byte[]>();
    
    log.debug("[<] getting ["+numMessages+"] bulk message");
    for(int i=0; i<numMessages; i++) {
      log.debug("reading message #["+i+"]");
      retval.add( readSimpleMessage(dis) );
    }
    
    return retval;
  }

  public static CsvMessage splitHeader(String msg) {
    String[] data = msg.split("\\r?\\n", 2);
    return new CsvMessage(data[0], data[1]);
  }
}
