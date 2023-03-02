package com.r_n_m.kws.Regulations._entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.r_n_m.kws.Regulations._enum.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visits")
public class Visit {

    public static final String collection = "visits";

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID entryId;
    private Timestamp timestamp;
    private Session session;
    private String plates;
    private int passengers;
    private String phone;
    private Account account;

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit visit)) return false;

        if (getPassengers() != visit.getPassengers()) return false;
        if (getEntryId() != null ? !getEntryId().equals(visit.getEntryId()) : visit.getEntryId() != null) return false;
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
        int result = getEntryId() != null ? getEntryId().hashCode() : 0;
        result = 31 * result + (getTimestamp() != null ? getTimestamp().hashCode() : 0);
        result = 31 * result + (getSession() != null ? getSession().hashCode() : 0);
        result = 31 * result + (getPlates() != null ? getPlates().hashCode() : 0);
        result = 31 * result + getPassengers();
        result = 31 * result + (getPhone() != null ? getPhone().hashCode() : 0);
        result = 31 * result + (getAccount() != null ? getAccount().hashCode() : 0);
        return result;
    }

}
