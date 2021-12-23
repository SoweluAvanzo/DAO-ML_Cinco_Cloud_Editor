package info.scce.cincocloud.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;

public class CustomObjectMapperTest {

  private final ObjectMapper objectMapper = new CustomObjectMapper().objectMapper();

  @ParameterizedTest
  @CsvSource({
      "444,       000000",
      "444444,    000444",
      "444444444, 444444",
  })
  public void writeValueAsString_instantWithNDigitsForNanoSeconds_trimToSixDigits(
      int nanoSeconds, String expectedDigits) throws Exception {

    final var test = new TestInstantClass();
    test.instant = LocalDateTime
        .of(2069, 4, 20, 1, 2, 3, nanoSeconds)
        .atZone(ZoneId.of("Europe/Paris"))
        .toInstant();

    final var formattedString = objectMapper.writeValueAsString(test);
    final var expectedFormattedString = "{\"instant\":\"2069-04-20T01:02:03." + expectedDigits + "Z\"}";
    JSONAssert.assertEquals(expectedFormattedString, formattedString, false);
  }

  private static class TestInstantClass {

    public Instant instant;
  }
}
