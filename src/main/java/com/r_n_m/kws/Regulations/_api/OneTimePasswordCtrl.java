package com.r_n_m.kws.Regulations._api;

import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._entities.OneTimePassword;
import com.r_n_m.kws.Regulations._exception.FailureException;
import com.r_n_m.kws.Regulations._exception.NotFoundException;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import com.r_n_m.kws.Regulations._interface.OneTimePasswordOps;
import com.r_n_m.kws.Regulations._interface.SmsOps;
import com.r_n_m.kws.Regulations._models.Forms;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
@RequestMapping(path = "otp")
@Slf4j
@RequiredArgsConstructor
public class OneTimePasswordCtrl extends Assistant {

    private final OneTimePasswordOps oneTimePasswordOps;
    private final AccountOps accountOps;
    private final SmsOps smsOps;

    @PostMapping
    void request_for_OTP(@RequestBody Forms.OtpRequest otpRequest) {
        val account = accountOps.get_user(otpRequest.account());
        if (account == null) {
            throw new NotFoundException("The User account provided does not exist");
        }

        val otp = oneTimePasswordOps.create_OTP(otpRequest.account());
        if (otp == null) {
            throw new FailureException("Unable to obtain a valid OTP, please contact us on this issue to support");
        }

        log.info("Generated an OTP for '{}' as '{}'", account.getUsername(), otp);

        Runnable runnable = () -> {
            var message = "Hello %s, your OTP code is %d and it expires in 1 (One) minute.".formatted(account.getName(), otp.getPasswordCode());
            var sentSms = smsOps.send_message(account.getPhone(), message);
            if (sentSms) {
                log.info("Successfully SENT an OTP for {} via SMS", account.getUsername());
            } else {
                log.warn("Failed to send the OTP for {} via SMS", account.getUsername());
            }
        };
        new Thread(runnable).start();
    }

    @PutMapping("/authorize")
    void authorise_OTP_user(@RequestBody OneTimePassword oneTimePassword) {
        log.info("Body = {}", oneTimePassword);

        if (accountOps.get_user(oneTimePassword.getAccountId()) == null) {
            throw new NotFoundException("The User account provided does not exist");
        }

        var otp = oneTimePasswordOps.get_OTP(oneTimePassword.getPasswordCode(), oneTimePassword.getAccountId());
        if (otp == null) {
            throw new NotFoundException("No valid OTP password found to authorize this request");
        }

        if (oneTimePasswordOps.deactivate_used_OTP(otp)) {
            log.info("Deactivated the OTP = '{}'", otp);
        } else {
            log.warn("Unable to deactivate the OTP = '{}'", otp);
        }

        var secondsElapsed = calculate_days_between_given_dates(otp.getExpiry(), new Date(), ChronoUnit.SECONDS);
        log.info("Time lapsed = '{}'", secondsElapsed);
        if (secondsElapsed > 60) {
            throw new FailureException("Sorry, the otp pass code has expired");
        }

        log.info("A user with the OTP = '{}' has been authorized", otp);
    }


}
