package info.scce.pyro.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.lang.reflect.Method;


public class PyroSelectiveRestFilter extends SimpleBeanPropertyFilter {

	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
			throws Exception {

		final JsonRenderIndicator renderIndicator = writer.getAnnotation(JsonRenderIndicator.class);

		if (renderIndicator == null) {
			super.serializeAsField(pojo, jgen, provider, writer);
		}
		else {
			final String checkMethod = renderIndicator.value();
			final Method setChecker = pojo.getClass().getMethod(checkMethod);
			final boolean isPropertySet = (Boolean) setChecker.invoke(pojo);

			if (isPropertySet) {
				super.serializeAsField(pojo, jgen, provider, writer);
			}
			else if (!jgen.canOmitFields()){
				writer.serializeAsOmittedField(pojo, jgen, provider);
			}
		}
	}
}
