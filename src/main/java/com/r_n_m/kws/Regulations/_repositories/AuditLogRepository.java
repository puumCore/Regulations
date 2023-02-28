package com.r_n_m.kws.Regulations._repositories;

import com.r_n_m.kws.Regulations._entities.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

}
