package lior.lview;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

public class ConnectionManager
{
  private static Logger log = Logger.getLogger(ConnectionManager.class);

  ServerSocket server;
  LView lv;

  public void start(LView lv, int port) {
    try {
      createThreadPool(4);

      ServerSocket server = new ServerSocket(port);

      log.info("[)))] LView server listening");
      boolean done = false;
      while (!done) {
        Socket sock = server.accept();
        log.info("[i] Connection accepted from "
            + sock.getInetAddress().getHostName());

        openConnections.put(sock);
      }
    }
    catch (IOException ioException) {
      ioException.printStackTrace();
    }
    catch (InterruptedException interE) {
    }
    finally {
      threads.clear();
    }
  }

  private LinkedBlockingQueue<Socket> openConnections =
      new LinkedBlockingQueue<Socket>(10);
  private LinkedList<ConnectionHandler> threads =
      new LinkedList<ConnectionHandler>();

  private void createThreadPool(int numThreads) {
    for (int i = 0; i < numThreads; i++) {
      threads.add(new ConnectionHandler(openConnections));
    }
  }

  public static void main(String args[]) throws LViewException {
    LView lv = new LView();

    ConnectionManager mgr = new ConnectionManager();
    mgr.start(lv, 9090);
  }
}
