package com.r_n_m.kws.Regulations._entities;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "logs")
public class AuditLog {

    @MongoId
    private ObjectId logId;
    private String timestamp;
    private String principal;
    private String type;
    private JsonElement data;

}
