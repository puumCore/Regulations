package com.r_n_m.kws.Regulations._repositories;

import com.r_n_m.kws.Regulations._entities.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, UUID> {

}
