package com.r_n_m.kws.Regulations._custom;

import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._entities.AuditLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class UUIDGenerators {

    @Bean
    public BeforeConvertCallback<AuditLog> beforeLogSaveCallback() {
        return (entity, collection) -> {
            if (entity.getLogId() == null) {
                entity.setLogId(UUID.randomUUID());
            }
            return entity;
        };
    }

    @Bean
    public BeforeConvertCallback<Account> beforeAccountSaveCallback() {
        return (entity, collection) -> {
            if (entity.getAccountId() == null) {
                entity.setAccountId(UUID.randomUUID());
            }
            return entity;
        };
    }

}
