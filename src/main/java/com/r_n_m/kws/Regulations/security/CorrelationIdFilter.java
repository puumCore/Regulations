package com.r_n_m.kws.Regulations.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CorrelationIdFilter extends OncePerRequestFilter {

    // UUIDv4, matching either case, but depends on what format you want to use
    private static final Pattern UUID_PATTERN = Pattern.compile("([a-fA-F0-9]{8}(-[a-fA-F0-9]{4}){4}[a-fA-F0-9]{8})");

    private final ApplicationEventPublisher publisher;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var correlationId = request.getHeader("correlation-id");
        if (null == correlationId || !UUID_PATTERN.matcher(correlationId).matches()) {
            // only allow UUIDs, if it's not valid according to our contract, allow it to be rewritten
            // alternatively, we would reject the request with an HTTP 400 Bad Request, as a client
            // hasn't fulfilled the contract
            correlationId = UUID.randomUUID().toString();
        }
        publisher.publishEvent(new HttpRequestReceivedEvent(request, correlationId));
        // make sure that the Mapped Diagnostic Context (MDC) has the `correlationId` so it can then
        // be populated in the logs
        try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", correlationId)) {
            response.addHeader("correlation-id", correlationId);
            filterChain.doFilter(request, response);
        }
    }
}