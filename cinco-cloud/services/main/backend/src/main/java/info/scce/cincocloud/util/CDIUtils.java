package info.scce.cincocloud.util;

import jakarta.enterprise.inject.spi.CDI;

public class CDIUtils {

  public static <T> T getBean(Class<T> someClass) {
    return CDI.current().select(someClass).get();
  }
}
