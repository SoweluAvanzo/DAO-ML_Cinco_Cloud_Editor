package info.scce.cincocloud.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import jakarta.inject.Singleton;

public class CustomObjectMapper {

  @Singleton
  public ObjectMapper objectMapper() {
    final var mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    final var module = new SimpleModule();
    module.addSerializer(Instant.class, new Iso8601InstantSerializer());
    mapper.registerModule(module);

    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);

    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mapper.setFilterProvider(new SimpleFilterProvider()
        .addFilter("CincoCloud_Selective_Filter", new CincoCloudSelectiveRestFilter()));

    return mapper;
  }

  private static class Iso8601InstantSerializer extends JsonSerializer<Instant> {

    private final DateTimeFormatter df = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        .withZone(ZoneId.systemDefault());

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeString(df.format(value));
    }
  }
}
