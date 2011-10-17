package lior.lview;

import java.io.PrintWriter;

public class Logger
{
  private static PrintWriter logger = new PrintWriter(System.err, true);
  
  public static void setLogger(PrintWriter out) {
    Logger.logger = out;
  }

  public static void print(Object s) {
    logger.print(s);
  }
  
  public static void println(Object s) {
    logger.println(s);
  }
  
  public static void println() {
    logger.println();
  }

}
