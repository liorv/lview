package lior.lview.data;

import java.util.Arrays;
import java.util.List;

public class PersistedClasses
{
  public static List<Class<?>> list() {
    Class<?>[] classes =
        {
            Component.class,
            ComponentReport.class,
            Feature.class,
            Pkg.class,
            Requirement.class,
            Test.class,
            TestResult.class };

    return Arrays.asList(classes);
  }
}
