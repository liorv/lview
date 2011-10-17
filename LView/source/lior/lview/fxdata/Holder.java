package lior.lview.fxdata;

import lior.lview.data.ComponentReport;
import lior.lview.data.TestResult;

public class Holder
{
  // actions:
  // 1. made ID public, and renamed if to "myId" (maybe "id" is
  // 2. reserved by Flex?) -- FAIL
  // 3. remove id getters/setters, stopped calling
  // setId in constructor (because its gone) -- FAIL
  // 4. remove static call to createKey from constructor, and removed createKey
  // function
  // 5. add no-arg constructor -- FAIL
  // 6. remove arg-constructor and change to init method -- FAIL
  // 7. remove no-arg cons altogether (i.e. implied), removed init method --
  // FAIL
  // 8. remove all by myId field, definition now almost identical to Test1
  // 9. make CR identical to Test1 -- FAIL
  // 10. swap order of collections in Test2 -- FAIL
  // 11. swap names of variables tests/reports -- SUCCESS !!!!!!!!!!!!!!!!!
  // problem moved to tests from reports!!!
  // -> problem not related to var name
  // -> problem related to CR class...
  // 12. change order of instantiation of collection classes in cons of Test2 --
  // FAIL
  // 13. rename CR class to Test2 -- FAIL
  // 14. rename Test2 class to Test0
  // soln? what appears to work is manually registering class aliases in init
  // method... not holding my breath...

  public Holder() {

  }

  public TestResult tr = new TestResult("id", "version", "0486", "solaris",
      "oracle", "PASS", 1233, "");
  
  public ComponentReport cr = new ComponentReport("c", "ver", "build", "plat", "db");

  public Test1 t1 = new Test1();
  public Test2 t2 = new Test2();
}
