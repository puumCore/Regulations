package com.r_n_m.kws.Regulations._entities;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "logs")
public class AuditLog {

    @Id
    private UUID logId;
    private String timestamp;
    private String principal;
    private String type;
    private JsonElement data;

}
