package guru.sfg.brewery.filter.google;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.exception.handler.Google2FaFailureHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest.StaticResourceRequestMatcher;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources;

@Slf4j
@Component
@RequiredArgsConstructor
public class Google2FaFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private final Google2FaFailureHandler failureHandler = new Google2FaFailureHandler();
    private final RequestMatcher url2Fa = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher urlResource = new AntPathRequestMatcher("/resources/**");
    private final StaticResourceRequestMatcher staticResourceRequestMatcher = toStaticResources().atCommonLocations();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (url2Fa.matches(request) ||
                urlResource.matches(request) ||
                staticResourceRequestMatcher.matches(request)) {

            log.debug("Request is not for 2FA. Processing next filter.");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (nonNull(authentication) && !authenticationTrustResolver.isAnonymous(authentication)) {
            log.debug("User is authenticated. Processing Google 2FA filter.");

            if (nonNull(authentication.getPrincipal()) && authentication.getPrincipal() instanceof User) {

                User user = (User) authentication.getPrincipal();
                if (user.isGoogle2FaEnabled() && user.isGoogle2FaRequired()) {
                    log.debug("User is using Google 2FA.");
                    failureHandler.onAuthenticationFailure(request, response, null);
                    return;
                }
            }

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
