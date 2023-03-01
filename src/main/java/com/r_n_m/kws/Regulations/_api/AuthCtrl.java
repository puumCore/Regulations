package com.r_n_m.kws.Regulations._api;

import com.google.gson.JsonParser;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._exception.BadRequestException;
import com.r_n_m.kws.Regulations._exception.FailureException;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 19/07/2022
 */

@RestController
@RequestMapping(path = "iam")
@Slf4j(topic = "Controller: IAM")
public class AuthCtrl {

    @Autowired
    private AccountOps accountOps;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(path = "/self-register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    void self_registration(@RequestBody Account account) {
        log.info("body = {}", account);

        final var warning = account.get_warning();
        if (warning != null) {
            throw new BadRequestException(warning);
        }

        if (accountOps.the_username_already_exists(account.getUsername())) {
            throw new BadRequestException("The username is already taken, please consider a new one");
        }

        account.setAuthenticated(false);
        account.setAuthorised(true);
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        if (accountOps.create_account(account) == null) {
            throw new FailureException("Unable to self register the user");
        }
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void add_first_user(@RequestBody Account account) {
        log.info("body = {}", account);

        final var warning = account.get_warning();
        if (warning != null) {
            throw new BadRequestException(warning);
        }

        if (!accountOps.get_accounts().isEmpty()) {
            throw new BadRequestException("Sorry, this feature can only be used on fresh use.");
        }

        account.setAuthenticated(true);
        account.setAuthorised(true);
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        account = accountOps.create_account(account);
        if (account == null) {
            throw new FailureException("Something wrong happened and the account was NOT be saved");
        }
    }

    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String reset_account_password(@RequestBody String body) {
        log.info("body = {}", body);

        var jsonElement = JsonParser.parseString(body);
        if (jsonElement.isJsonNull() || !jsonElement.isJsonObject()) {
            throw new BadRequestException("No valid body has been provided");
        }
        var username = jsonElement.getAsJsonObject().get("username");
        if (username == null) {
            throw new BadRequestException("No username has been provided");
        }
        val account = accountOps.get_account(username.getAsString());
        if (account == null) {
            throw new BadRequestException("No user has the provided username");
        }
        var newPassword = accountOps.get_password();
        if (accountOps.the_password_has_been_updated(account.getUsername(), passwordEncoder.encode(newPassword))) {
            return newPassword;
        } else {
            throw new FailureException("Failed to update your account password, please ignore the password sent to your email address.");
        }
    }


}
