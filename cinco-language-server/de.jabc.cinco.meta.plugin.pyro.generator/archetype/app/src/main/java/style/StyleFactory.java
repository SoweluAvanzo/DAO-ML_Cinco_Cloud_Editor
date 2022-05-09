package style;

import style.impl.StyleFactoryImpl;

public interface StyleFactory {

    StyleFactory eINSTANCE = StyleFactoryImpl.init();


    Color createColor();

    Appearance createAppearance();

    Font createFont();
}

