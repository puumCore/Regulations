package com.r_n_m.kws.Regulations._entities;

import com.r_n_m.kws.Regulations._enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.0
 * @since 14/02/2023
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

    public transient static final String collection = "accounts";

    @MongoId
    private ObjectId accountId;

    @NonNull
    private String name;
    @NonNull
    private String phone;
    @NonNull
    private Role role;
    @NonNull
    private String username;
    @NonNull
    private String password;
    private boolean isAuthenticated;
    private boolean isAuthorised;

}
