package lior.lview.message;

import lior.lview.LViewException;
import lior.lview.jdo.ClearAllAction;

public class ClearAllProcessor extends MessageProcessor
{
  @Override
  public void process(String msg) throws LViewException {
    (new ClearAllAction()).perform();
  }

}
