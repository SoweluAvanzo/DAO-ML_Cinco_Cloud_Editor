package info.scce.pyro.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.quarkus.jackson.ObjectMapperCustomizer;
import javax.inject.Singleton;
import info.scce.pyro.util.Constants;

@Singleton
public class RegisterCustomModuleCustomizer implements ObjectMapperCustomizer {
	
    public void customize(ObjectMapper mapper) {
		mapper.findAndRegisterModules();

		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
				.builder()
				.allowIfBaseType(RESTBaseType.class)
				.build();
		mapper.activateDefaultTypingAsProperty(ptv,ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, Constants.PYRO_RUNTIME_TYPE);
		
		PolymorphyRegistrator.registerSubTypes(mapper);
				
		mapper.setFilterProvider(new SimpleFilterProvider().addFilter("PYRO_Selective_Filter", new PyroSelectiveRestFilter()));
    }
}