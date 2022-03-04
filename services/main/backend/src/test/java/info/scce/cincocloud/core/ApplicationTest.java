package info.scce.cincocloud.core;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.db.SettingsDB;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ApplicationTest extends AbstractCincoCloudTest {

  @BeforeEach
  public void setup() {
    reset();
  }

  @Test
  public void getSettings_exists() {
    final List<SettingsDB> settingsList = SettingsDB.findAll().list();
    assertEquals(1, settingsList.size());
  }

  @Test
  public void getSettings_allowPublicUserRegistrationIsTrue() {
    final Optional<SettingsDB> settings = SettingsDB.findAll().list()
        .stream().map(s -> (SettingsDB) s)
        .findFirst();

    assertTrue(settings.isPresent());
    assertTrue(settings.get().allowPublicUserRegistration);
  }
}
