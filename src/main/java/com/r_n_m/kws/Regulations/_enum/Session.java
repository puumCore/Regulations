package com.r_n_m.kws.Regulations._enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Session {

    MORNING("Morning 6am - 12noon"),
    EVENING("Evening 12noon - 6pm"),
    FULL_DAY("Full day 6am - 6pm");

    private final String info;

}
