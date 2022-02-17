package info.scce.cincocloud.core;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.auth.TokenUtils;
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
  PBKDF2Encoder passwordEncoder;

  public AuthResponseTO login(UserLoginInput login) {
    final UserDB user = UserDB.find("email", login.email).firstResult();
    if (user == null) {
      throw new RestException(Status.UNAUTHORIZED, "the user could not be found");
    }

    if (!user.password.equals(passwordEncoder.encode(login.password))) {
      throw new RestException(Status.UNAUTHORIZED, "the password is not correct");
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
      return new AuthResponseTO(TokenUtils.generateToken(user.email, roles, duration, issuer));
    } catch (Exception e) {
      throw new RestException(Status.UNAUTHORIZED, "failed to generate token");
    }
  }
}
