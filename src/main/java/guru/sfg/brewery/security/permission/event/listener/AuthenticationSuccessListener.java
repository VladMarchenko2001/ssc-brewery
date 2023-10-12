package guru.sfg.brewery.security.permission.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationSuccessListener {

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        log.info("Authentication was successful for user: {}", event.getAuthentication().getName());
    }
}
