package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.auth.TokenUtils;
import info.scce.cincocloud.config.Properties;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import info.scce.cincocloud.exeptions.RestException;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;
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

  public AuthResponseTO login(UserLoginInput login) {
    final var userByEmail = (UserDB) UserDB.find("email", login.emailOrUsername).firstResult();
    final var userByUsername = (UserDB) UserDB.find("username", login.emailOrUsername).firstResult();
    if (userByEmail == null && userByUsername == null) {
      throw new RestException(Status.UNAUTHORIZED, "Invalid Credentials!");
    }

    final var user = userByEmail != null ? userByEmail : userByUsername;
    if (!user.password.equals(passwordEncoder.encode(login.password))) {
      throw new RestException(Status.UNAUTHORIZED, "Invalid Credentials!");
    }

    return generateToken(user);
  }

  public AuthResponseTO generateToken(UserDB user) {
    try {
      final var roles = new HashSet<String>();
      roles.add("user");
      if (user.systemRoles.contains(UserSystemRole.ADMIN)) {
        roles.add("admin");
      }

      final var token = TokenUtils.generateToken(user.email, roles, duration, issuer, properties.getAuthPrivateKey());
      return new AuthResponseTO(token);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RestException(Status.UNAUTHORIZED, "failed to generate token");
    }
  }
}
