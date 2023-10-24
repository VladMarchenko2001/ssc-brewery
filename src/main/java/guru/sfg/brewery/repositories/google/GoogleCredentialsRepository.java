package guru.sfg.brewery.repositories.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GoogleCredentialsRepository implements ICredentialRepository {

    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String userName) {
        return userRepository.findByUsername(userName).orElseThrow().getGoogle2FaSecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        userRepository.findByUsername(userName).ifPresent(user ->
            userRepository.save(
                    user.setGoogle2FaSecret(secretKey)
                            .setGoogle2FaEnabled(true)));
    }
}
