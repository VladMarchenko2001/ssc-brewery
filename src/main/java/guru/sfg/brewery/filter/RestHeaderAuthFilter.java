package guru.sfg.brewery.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        log.debug("Request is to process authentication");

        try {
            Authentication authResult = attemptAuthentication(request, response);

            if (isNull(authResult)) chain.doFilter(request, response);
            else successfulAuthentication(request, response, chain, authResult);
        } catch (AuthenticationException e) {
            log.error("Authentication request failed: " + e.getMessage());
            unsuccessfulAuthentication(request, response, e);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String username = getSafeUsername(request);
        String password = getSafePassword(request);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        log.info("Env: " + this.getEnvironment());

        log.info("Filter name: " + this.getFilterName());

        if (isEmpty(username)) return null;
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);

        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        response.sendError(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase());
    }

    private String getSafeUsername(HttpServletRequest request) {
        String header = request.getHeader("Api-Key");
        return isEmpty(header) ? "" : header;
    }

    private String getSafePassword(HttpServletRequest request) {
        String header = request.getHeader("Api-Secret");
        return isEmpty(header) ? "" : header;
    }
}
