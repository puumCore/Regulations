package com.r_n_m.kws.Regulations._custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("SpellCheckingInspection")
@Getter
@RequiredArgsConstructor
public enum SmsConfig {

    BASE_URL("https://api.africastalking.com/version1/messaging"),
    APP("JC_HUB"),
    APP_USERNAME("NL-V1"),
    API_KEY("629888012c4e213ef23ab60332da234b1f1beebcb86bca0e1838807b04d05ee3");

    private final String value;

}
