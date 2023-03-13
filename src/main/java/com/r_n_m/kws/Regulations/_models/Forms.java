package com.r_n_m.kws.Regulations._models;

import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public abstract class Forms {


    public record OtpRequest(@NonNull UUID account) {
    }

    public record Auth(@NonNull String username, boolean enable) implements Serializable {

        @Serial
        private static final long serialVersionUID = 42L;

        @Override
        public String toString() {
            return new StringJoiner(", ", Auth.class.getSimpleName() + "[", "]")
                    .add("username='" + username + "'")
                    .add("enable=" + enable)
                    .toString();
        }
    }

}
