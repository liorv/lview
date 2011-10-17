package lior.lview;

import java.io.*;
import java.net.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

public class SOAPClient
{
  private static void parse(InputStream isr, DefaultHandler saxHandler) {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();

      saxParser.parse(isr, saxHandler);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void send(String soapUrl, String soapMsg,
      DefaultHandler saxHandler) throws Exception
  {
    byte[] b = soapMsg.getBytes();

    // Create the connection where we're going to send the file.
    URL url = new URL(soapUrl);
    URLConnection connection = url.openConnection();
    HttpURLConnection httpConn = (HttpURLConnection) connection;

    // Set the appropriate HTTP parameters.
    httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
    httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
    // httpConn.setRequestProperty("SOAPAction", SOAPAction);
    httpConn.setRequestMethod("POST");
    httpConn.setDoOutput(true);
    httpConn.setDoInput(true);

    // Everything's set up; send the XML that was read in to b.
    OutputStream out = httpConn.getOutputStream();
    out.write(b);
    out.close();

    // Read the response and write it to standard out.
    InputStream isr = httpConn.getInputStream();
    parse(isr, saxHandler);
  }
}
