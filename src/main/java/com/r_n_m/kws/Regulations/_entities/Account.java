package com.r_n_m.kws.Regulations._entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.r_n_m.kws.Regulations._enum.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

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

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID accountId;

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

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;

        if (isAuthenticated() != account.isAuthenticated()) return false;
        if (isAuthorised() != account.isAuthorised()) return false;
        if (getAccountId() != null ? !getAccountId().equals(account.getAccountId()) : account.getAccountId() != null)
            return false;
        if (getName() != null ? !getName().equals(account.getName()) : account.getName() != null) return false;
        if (getPhone() != null ? !getPhone().equals(account.getPhone()) : account.getPhone() != null) return false;
        if (getRole() != account.getRole()) return false;
        if (getUsername() != null ? !getUsername().equals(account.getUsername()) : account.getUsername() != null)
            return false;
        return getPassword() != null ? getPassword().equals(account.getPassword()) : account.getPassword() == null;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public int hashCode() {
        int result = getAccountId() != null ? getAccountId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (isAuthenticated() ? 1 : 0);
        result = 31 * result + (isAuthorised() ? 1 : 0);
        return result;
    }

}
