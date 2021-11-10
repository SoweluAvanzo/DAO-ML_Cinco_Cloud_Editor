package info.scce.cincocloud.util;

import javax.enterprise.inject.spi.CDI;

public class CDIUtils {

    public static <T> T getBean(Class<T> tClass) {
        return CDI.current().select(tClass).get();
    }
}
