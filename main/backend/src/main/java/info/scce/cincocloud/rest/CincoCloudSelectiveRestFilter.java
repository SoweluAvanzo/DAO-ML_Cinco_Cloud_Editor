package info.scce.cincocloud.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import java.lang.reflect.Method;

public class CincoCloudSelectiveRestFilter extends SimpleBeanPropertyFilter {

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jsonGenerator, SerializerProvider provider, PropertyWriter writer)
            throws Exception {

        final JsonRenderIndicator renderIndicator = writer.getAnnotation(JsonRenderIndicator.class);

        if (renderIndicator == null) {
            super.serializeAsField(pojo, jsonGenerator, provider, writer);
        } else {
            final String checkMethod = renderIndicator.value();
            final Method setChecker = pojo.getClass().getMethod(checkMethod);
            final boolean isPropertySet = (Boolean) setChecker.invoke(pojo);

            if (isPropertySet) {
                super.serializeAsField(pojo, jsonGenerator, provider, writer);
            } else if (!jsonGenerator.canOmitFields()) {
                writer.serializeAsOmittedField(pojo, jsonGenerator, provider);
            }
        }
    }
}
