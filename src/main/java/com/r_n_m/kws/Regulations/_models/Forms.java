package com.r_n_m.kws.Regulations._models;

import lombok.NonNull;

import java.util.StringJoiner;

public abstract class Forms {

    public record Auth(@NonNull String username, boolean enable) {
        @Override
        public String toString() {
            return new StringJoiner(", ", Auth.class.getSimpleName() + "[", "]")
                    .add("username='" + username + "'")
                    .add("enable=" + enable)
                    .toString();
        }
    }

}
