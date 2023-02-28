package com.r_n_m.kws.Regulations.security;

import com.google.gson.GsonBuilder;
import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._entities.AuditLog;
import com.r_n_m.kws.Regulations._repositories.AuditLogRepository;
import com.r_n_m.kws.Regulations._util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "AuditLogger")
public class LoggingAuditEventListener extends Assistant {

    @Autowired
    private AuditLogRepository auditLogRepository;


    @EventListener
    public void on(AuditApplicationEvent event) {
        final var backup = MDC.getCopyOfContextMap();
        final var auditEvent = event.getAuditEvent();
        MDC.put("event.type", auditEvent.getType());
        MDC.put("event.principal", auditEvent.getPrincipal());

        final var auditLog = new AuditLog();
        auditLog.setTimestamp(DateFormatUtils.format(DateUtils.asDate(auditEvent.getTimestamp().atZone(clock.getZone()).toLocalDateTime()), "yyyy-MMM-dd HH:mm:ss"));
        auditLog.setPrincipal(auditEvent.getPrincipal());
        auditLog.setType(auditEvent.getType());
        auditLog.setData(new GsonBuilder().setPrettyPrinting().create().toJsonTree(auditEvent.getData()));
        try {
            auditLogRepository.insert(auditLog);
        } catch (Exception e) {
            log.error("Failed to log audit", e);
        }

        if (backup != null) {
            MDC.setContextMap(backup);
        }
    }

    //Uncomment when you need to audit specific events
    /*@EventListener
    public void on(AbstractAuthorizationEvent abstractEvent) {
        var backup = MDC.getCopyOfContextMap();
        if (abstractEvent instanceof AuthorizationFailureEvent) {
            AuthorizationFailureEvent event = (AuthorizationFailureEvent) abstractEvent;
            MDC.put("event.type", "AUTHORIZATION_FAILURE_EVENT");
            MDC.put("event.principal", event.getAuthentication().getName());
            FilterInvocation filterInvocation = (FilterInvocation) event.getSource();
            MDC.put("source.requestUrl", filterInvocation.getRequestUrl());
        }
        // and other checks for other subclasses
        LOGGER.info("An AuthorizationFailureEvent was received: {}", abstractEvent);

        if (backup != null) {
            MDC.setContextMap(backup);
        }
    }*/

}