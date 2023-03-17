package info.scce.cincocloud.core.services;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.auth.TokenUtils;
import info.scce.cincocloud.config.Properties;
import info.scce.cincocloud.db.BaseFileDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.sync.ticket.TicketRegistrationHandler;
import io.quarkus.security.UnauthorizedException;
import java.util.HashSet;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Transactional
public class AuthService {

  @ConfigProperty(name = "cincocloud.jwt.duration")
  Long duration;

  @ConfigProperty(name = "mp.jwt.verify.issuer")
  String issuer;

  @Inject
  Properties properties;

  @Inject
  PBKDF2Encoder passwordEncoder;

  public String login(String usernameOrEmail, String password) {
    final var userByEmail = (UserDB) UserDB.find("email", usernameOrEmail).firstResult();
    final var userByUsername = (UserDB) UserDB.find("username", usernameOrEmail).firstResult();
    if (userByEmail == null && userByUsername == null) {
      throw new UnauthorizedException("Invalid Credentials!");
    }

    final var user = userByEmail != null ? userByEmail : userByUsername;

    if (!user.password.equals(passwordEncoder.encode(password))) {
      throw new UnauthorizedException("Invalid Credentials!");
    }

    if (!user.isActivated) {
      throw new UnauthorizedException("Account is not activated!");
    }

    return generateToken(user);
  }

  public void logout(UserDB user) {
    TicketRegistrationHandler.removeTicketsOf(user);
  }

  public String generateToken(UserDB user) {
    try {
      final var roles = new HashSet<String>();
      roles.add("user");
      if (user.systemRoles.contains(UserSystemRole.ADMIN)) {
        roles.add("admin");
      }

      return TokenUtils.generateToken(user.email, roles, duration, issuer, properties.getAuthPrivateKey());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to generate token.");
    }
  }
}
