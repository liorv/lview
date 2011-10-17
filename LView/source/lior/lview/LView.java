package lior.lview;

import org.apache.log4j.Logger;

import lior.lview.fxdata.*;

public class LView
{
  private static Logger log = Logger.getLogger(LView.class);
  
  public Holder getStuff() {
    log.info("DEBUG: stuff");
    return new Holder();
  }

  public ComponentReportResult collectComponentReportData() {
    return new ComponentReportResult();
  }

  public ComponentTestData getTestData(String component, String[] builds, String[] platforms) {
    return new ComponentTestData(component, builds, platforms);
  }
}
