package guru.sfg.brewery.web.controllers.user;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static guru.sfg.brewery.config.google.GoogleAuthConfig.ISSUER;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
public class Google2FaController {

    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2Fa(Model model) {
        User user = getUser();
        String otpAuthTotpURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                ISSUER,
                user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("otpAuthTotpURL: " + otpAuthTotpURL);

        model.addAttribute("googleurl", otpAuthTotpURL);
        return "user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2Fa(@RequestParam Integer verifyCode) {
        User user = getUser();

        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            userRepository.findById(user.getId())
                    .ifPresent(dbUser -> userRepository.save(dbUser.setGoogle2FaEnabled(true)));
            return "/index";
        } else {
            return "user/register2fa";
        }
    }

    @GetMapping("/verify2fa")
    public String goToVerify2Fa() {
        return "user/verify2fa";
    }

    @PostMapping("/verify2fa")
    public String verify2Fa(@RequestParam Integer verifyCode) {
        User user = getUser();

        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            getUser().setGoogle2FaRequired(false);
            return "/index";
        } else {
            return "user/verify2fa";
        }
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
