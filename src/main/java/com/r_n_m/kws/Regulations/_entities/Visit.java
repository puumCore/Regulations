package com.r_n_m.kws.Regulations._entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.r_n_m.kws.Regulations._enum.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visits")
public class Visit {

    public static final String collection = "visits";

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID visitId;
    private Date timestamp;
    private Session session;
    private String plates;
    private int passengers;
    private String phone;
    private Account account;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public final String get_warning() {
        if (this.getSession() == null) {
            return "Please provide the visiting session then retry.";
        }
        if (this.getPlates() == null || this.getPlates().isBlank()) {
            return "Please provide the vehicle plates then retry.";
        }
        if (this.getPassengers() < 1) {
            return "Please provide the correct number of passengers then retry.";
        }
        if (this.getPhone() == null || this.getPhone().isBlank()) {
            return "Please provide the phone number of the account holder then retry.";
        }
        if (this.getAccount() == null) {
            return "Please provide the information of the account information of the user providing this information then retry.";
        }
        return this.getAccount().get_warning(false);
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit visit)) return false;

        if (getPassengers() != visit.getPassengers()) return false;
        if (getVisitId() != null ? !getVisitId().equals(visit.getVisitId()) : visit.getVisitId() != null) return false;
        if (getTimestamp() != null ? !getTimestamp().equals(visit.getTimestamp()) : visit.getTimestamp() != null)
            return false;
        if (getSession() != visit.getSession()) return false;
        if (getPlates() != null ? !getPlates().equals(visit.getPlates()) : visit.getPlates() != null) return false;
        if (getPhone() != null ? !getPhone().equals(visit.getPhone()) : visit.getPhone() != null) return false;
        return getAccount() != null ? getAccount().equals(visit.getAccount()) : visit.getAccount() == null;
    }

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public int hashCode() {
        int result = getVisitId() != null ? getVisitId().hashCode() : 0;
        result = 31 * result + (getTimestamp() != null ? getTimestamp().hashCode() : 0);
        result = 31 * result + (getSession() != null ? getSession().hashCode() : 0);
        result = 31 * result + (getPlates() != null ? getPlates().hashCode() : 0);
        result = 31 * result + getPassengers();
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getAccount() != null ? getAccount().hashCode() : 0);
        return result;
    }

}
