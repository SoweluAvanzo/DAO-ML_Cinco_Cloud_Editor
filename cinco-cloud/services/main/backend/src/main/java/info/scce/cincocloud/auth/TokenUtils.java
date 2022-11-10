package info.scce.cincocloud.auth;

import io.smallrye.jwt.build.Jwt;

import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

/**
 * Utilities for generating a JWT for testing
 */
public class TokenUtils {

  private static final String KEY_ID = "kid1";

  public static String generateToken(String username, Set<String> roles, Long duration, String issuer, String pk)
      throws Exception {
    final var claimsBuilder = Jwt.claims();
    final var currentTimeSeconds = currentTimeSeconds();

    claimsBuilder.issuer(issuer);
    claimsBuilder.subject(username);
    claimsBuilder.issuedAt(currentTimeSeconds);
    claimsBuilder.expiresAt(currentTimeSeconds + duration);
    claimsBuilder.groups(roles);

    final var pkcs8Pem = removeBeginEnd(pk);
    final var pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);
    final var keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
    final var keyFactory = KeyFactory.getInstance("RSA");
    final var privateKey = keyFactory.generatePrivate(keySpec);

    return claimsBuilder.jws().keyId(KEY_ID).sign(privateKey);
  }

  private static int currentTimeSeconds() {
    long currentTimeMS = System.currentTimeMillis();
    return (int) (currentTimeMS / 1000);
  }

  private static String removeBeginEnd(String pem) {
    pem = pem.replaceAll("-----BEGIN .*?-----", "");
    pem = pem.replaceAll("-----END .*?-----", "");
    pem = pem.replaceAll("\r\n", "");
    pem = pem.replaceAll("\n", "");
    return pem.trim();
  }
}
