package guru.sfg.brewery.config.google;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import com.warrenstrange.googleauth.ICredentialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class GoogleAuthConfig {

    @Value("${google.authenticator.issuer}")
    public static String ISSUER;

    @Bean
    public GoogleAuthenticator googleAuthenticator(ICredentialRepository iCredentialRepository) {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(SECONDS.toMillis(60))
                .setWindowSize(10)
                .setNumberOfScratchCodes(0)
                .build();
        GoogleAuthenticator authenticator = new GoogleAuthenticator(config);
        authenticator.setCredentialRepository(iCredentialRepository);
        return authenticator;
    }
}
