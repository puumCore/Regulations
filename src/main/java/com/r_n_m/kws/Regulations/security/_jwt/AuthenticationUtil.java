package com.r_n_m.kws.Regulations.security._jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import com.r_n_m.kws.Regulations.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Handles real time Authentication
 *
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 18/07/2022
 */

@Slf4j
@RequiredArgsConstructor
@Getter
public class AuthenticationUtil extends UsernamePasswordAuthenticationFilter {

    public static final Algorithm ALGORITHM = Algorithm.HMAC512(SecurityConfig.s3cr3t);
    private final AuthenticationManager authenticationManager;
    private final AccountOps accountOps;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        val username = request.getParameter("username");
        val password = request.getParameter("password");
        val authenticationToken = new UsernamePasswordAuthenticationToken(username.trim(), password.trim());
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        val nairobiZone = ZoneId.of("Africa/Nairobi");
        val clock = Clock.system(nairobiZone);
        val zoneOffset = nairobiZone.getRules().getOffset(LocalDateTime.now(clock));

        User user = (User) authResult.getPrincipal();
        val accessToken = JWT.create()
                .withSubject("KWS Regulations")
                .withAudience(user.getUsername())
                .withIssuedAt(LocalDateTime.now(clock).toInstant(zoneOffset))
                .withExpiresAt(LocalDateTime.now(clock).plusHours(3).toInstant(zoneOffset))
                .withIssuer(request.getContextPath())
                .withClaim("user", user.getUsername())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(ALGORITHM);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setHeader("accessToken", accessToken);

        val account = accountOps.get_account(user.getUsername());
        if (account != null) {
            new ObjectMapper().writeValue(response.getOutputStream(), account);
        }
    }

}
