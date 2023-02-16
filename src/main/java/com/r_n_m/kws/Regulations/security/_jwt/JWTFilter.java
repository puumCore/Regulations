package com.r_n_m.kws.Regulations.security._jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle's real time authorization.
 *
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 18/07/2022
 */

@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    @NonNull
    private final AccountOps accountOps;

    /**
     * Can be overridden in subclasses for custom filtering control,
     * returning {@code true} to avoid filtering of the given request.
     * <p>The default implementation always returns {@code false}.
     *
     * @param request current HTTP request
     * @return whether the given request should <i>not</i> be filtered
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        final var servletPath = request.getServletPath();
        return servletPath.startsWith("/iam");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final var authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            final var jwtToken = authHeader.substring("Bearer ".length());
            if (jwtToken.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to generate the right token then place where it has been advised");
            } else {
                try {
                    final JWTVerifier verifier = JWT.require(AuthenticationUtil.ALGORITHM).withSubject("KWS Regulations").build();
                    final DecodedJWT decodedJWT = verifier.verify(jwtToken);
                    final var username = decodedJWT.getClaim("user").asString();
                    if (username == null) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The access token provided is invalid; It lacks credentials to work with");
                        return;
                    }
                    if (accountOps.get_account(username) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The access token provided is invalid; It appears alien to the system");
                        return;
                    }
                    final var roles = decodedJWT.getClaim("roles").asArray(String.class);
                    if (roles == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The token provided is invalid; Could not decode your credentials");
                        return;
                    }
                    final var grantedAuthorities = Stream.of(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    //Please note that at this stage only authorization is required hence the password is not required.
                    final var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }

                    filterChain.doFilter(request, response);
                } catch (JWTVerificationException exc) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exc.getLocalizedMessage());
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authentication is required to continue.");
        }
    }

}
