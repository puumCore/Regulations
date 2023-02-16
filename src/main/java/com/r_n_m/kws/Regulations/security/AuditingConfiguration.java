package com.r_n_m.kws.Regulations.security;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditingConfiguration {

    @Bean
    public AuditEventRepository auditEventRepository() {
        // constructor also takes a default number of items to store in-memory for tuning
        return new InMemoryAuditEventRepository();
    }

}