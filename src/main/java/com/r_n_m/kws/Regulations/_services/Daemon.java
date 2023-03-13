package com.r_n_m.kws.Regulations._services;

import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._interface.OneTimePasswordOps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j(topic = "Background Jobs")
public class Daemon extends Assistant {

    private final OneTimePasswordOps oneTimePasswordOps;

    @Scheduled(fixedRate = 65, timeUnit = TimeUnit.SECONDS)
    void handle_unused_OTPs() {
        var deactivatedOtPs = oneTimePasswordOps.get_deactivated_OTPs();
        if (deactivatedOtPs != 0) {
            log.info("Deactivated '{}' invalid OTPs", deactivatedOtPs);
        }
    }

}
