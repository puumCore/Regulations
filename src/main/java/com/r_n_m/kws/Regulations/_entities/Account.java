package com.r_n_m.kws.Regulations._entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.r_n_m.kws.Regulations._enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public static final String collection = "accounts";

    @MongoId
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ObjectId accountId;

    private String name;
    private String phone;
    private Role role;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isAuthenticated;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isAuthorised;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public final String get_warning() {
        if (this.getName() == null || this.getName().isBlank()) {
            return "Please provide the name of the account holder then retry.";
        }
        if (this.getPhone() == null || this.getPhone().isBlank()) {
            return "Please provide the phone number of the account holder then retry.";
        }
        if (this.getRole() == null) {
            return "Please provide the role of the account holder then retry.";
        }
        if (this.getUsername() == null || this.getUsername().isBlank()) {
            return "Please provide the username of the account holder then retry.";
        }
        if (this.getPassword() == null || this.getPassword().isBlank()) {
            return "Please provide the password of the account holder then retry.";
        }
        return null;
    }

    public String getAccountId() {
        return accountId != null ? accountId.toHexString() : null;
    }

}
