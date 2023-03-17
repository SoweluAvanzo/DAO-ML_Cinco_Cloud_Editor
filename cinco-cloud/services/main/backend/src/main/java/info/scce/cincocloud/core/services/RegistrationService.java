package info.scce.cincocloud.core.services;

import info.scce.cincocloud.auth.PBKDF2Encoder;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.db.UserSystemRole;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.runtime.LaunchMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class RegistrationService {

    @Inject
    PBKDF2Encoder passwordEncoder;

    @Inject
    UserService userService;

    @Inject
    SettingsService settingsService;

    @Inject
    Mailer mailer;

    @Inject
    Template activationMail;

    @ConfigProperty(name = "cincocloud.host")
    String host;

    @ConfigProperty(name = "quarkus.http.root-path")
    String httpRootPath;

    @ConfigProperty(name = "cincocloud.ssl")
    boolean ssl;

    public UserDB registerUser(String name, String username, String email, String password) {
        final var user = userService.create(email, name, username, passwordEncoder.encode(password));

        if (UserDB.count() == 1) { // the first user of Cinco Cloud is the admin user.
            user.systemRoles.add(UserSystemRole.ADMIN);
            userService.activateUser(user, false);
        } else {
            final var settings = settingsService.getSettings();
            if (settings.autoActivateUsers) {
                userService.activateUser(user, true);
            } else {
                if (settings.sendMails) {
                    mailer.send(
                            Mail.withHtml(
                                    email,
                                    "Cinco Cloud: Activate your user account.",
                                    getActivationMailString(user)));
                }
            }
        }

        return user;
    }

    private String getActivationMailString(UserDB user) {
        return activationMail
                .data(
                        "url", getCincoCloudPath(),
                        "token", user.activationKey,
                        "userId", user.id)
                .render();
    }

    public String getCincoCloudPath() {
        return (ssl ? "https://" : "http://")
                + host
                + httpRootPath
                + (httpRootPath.endsWith("/") ? "" : "/")
                + (LaunchMode.current().equals(LaunchMode.DEVELOPMENT) ? "frontend/" : "");
    }
}
