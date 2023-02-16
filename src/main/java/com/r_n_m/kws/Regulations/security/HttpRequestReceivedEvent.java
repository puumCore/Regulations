package com.r_n_m.kws.Regulations.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class HttpRequestReceivedEvent extends AuditApplicationEvent {

    public HttpRequestReceivedEvent(HttpServletRequest request, String correlationId) {
        super(principal(request), "HTTP_REQUEST_RECEIVED", details(request, correlationId));
    }

    private static String principal(HttpServletRequest request) {
        return Optional.ofNullable(request.getUserPrincipal())
                .map(Principal::getName)
                .orElse("anonymousUser");
    }

    private static Map<String, Object> details(HttpServletRequest request, String correlationId) {
        SortedMap<String, Object> details = new TreeMap<>();
        details.put("1 http.correlationId", correlationId);
        details.put("2 http.method", request.getMethod());
        details.put("3 http.servletPath", request.getServletPath());
        details.put("4 http.remoteHost", request.getRemoteHost());
        details.put("5 http.remotePort", request.getRemotePort());
        details.put("6 http.requestURL", request.getRequestURL());
        details.put("7 http.token", request.getHeader("Authorization") == null ? "Token not found" : "Token Found");
        // other details here
        return details;
    }

}