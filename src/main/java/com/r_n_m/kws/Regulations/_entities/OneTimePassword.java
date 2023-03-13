package com.r_n_m.kws.Regulations._entities;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@Document(collection = "otp")
public class OneTimePassword implements Serializable {

    @Serial
    private static final long serialVersionUID = 42L;

    @Id
    private UUID otpId;
    @NonNull
    private final Integer passwordCode;
    @NonNull
    private final UUID accountId;
    private Date expiry;
    private boolean deactivated;

}
