package lior.lview;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;

import org.apache.log4j.Logger;

import lior.lview.message.MessageProcessor;
import lior.lview.message.MessageProcessor.CsvMessage;

public class ConnectionHandler extends Thread
{
  private static Logger log = Logger.getLogger(ConnectionHandler.class);
  
  DataInputStream dis;
  DataOutputStream dos;
  LinkedBlockingQueue<Socket> todoList;

  public ConnectionHandler(LinkedBlockingQueue<Socket> todoList) {
    super();

    this.todoList = todoList;
    start();
  }

  public void run() {
    Socket sock;
    try {
      while (true) {
        sock = todoList.take();
        handle(sock);
      }
    }
    catch (InterruptedException e) {
    }
  }

  public void handle(Socket sock) {
    try {
      dis = new DataInputStream(sock.getInputStream());
      dos = new DataOutputStream(sock.getOutputStream());

      byte[] msg = MessageProcessor.readSimpleMessage(dis);
      String response = processMessage(msg, dis);
      writeMessage("0" + ((response == null) ? "" : response));
    }
    catch (Exception e) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        log.error("[E] " + sw.getBuffer().toString());
        writeMessage("1" + sw.getBuffer().toString());
      }
      catch (IOException e2) {
        log.error("[E] failed to transmit error message \n\t["
            + e2.getMessage() + "]");
      }
    }
    log.info("DONE! " + getName());
  }

  public static String processMessage(byte[] msgBytes,
      DataInputStream dis) throws LViewException
  {
    CsvMessage csvMsg = MessageProcessor.splitHeader(new String(msgBytes));

    String[] headerArr = csvMsg.header.split(",", -1);
    if (headerArr.length < 1)
      throw new LViewException(
          "Message does not contain type information in header");
    String msgProcClassName = headerArr[0];

    try {
      MessageProcessor msgProc = getMessageProcessor(msgProcClassName);
      msgProc.process(csvMsg.body, dis);
      return msgProc.getResponse();
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LViewException("failed to process message [" + msgProcClassName
          + "]\n\treason=[" + e.getMessage() + "]\n");
    }
  }

  private void writeMessage(String message) throws IOException {
    dos.writeInt(message.length());
    dos.writeBytes(message);
  }

  private static MessageProcessor getMessageProcessor(String msgProcClassName)
      throws ClassNotFoundException, IllegalAccessException,
      InstantiationException
  {
    MessageProcessor mp = i_procMap.get(msgProcClassName);
    if (mp == null) {
      Class<?> msgProcClass =
          Class.forName(ConnectionHandler.class.getPackage().getName()
              + ".message." + msgProcClassName + "Processor");

      mp = (MessageProcessor) msgProcClass.newInstance();
      i_procMap.put(msgProcClassName, mp);
    }
    return mp;
  }

  private static HashMap<String, MessageProcessor> i_procMap =
      new HashMap<String, MessageProcessor>();
}
