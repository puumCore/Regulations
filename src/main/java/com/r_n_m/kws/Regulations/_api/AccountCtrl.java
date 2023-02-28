package com.r_n_m.kws.Regulations._api;

import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._enum.Role;
import com.r_n_m.kws.Regulations._exception.BadRequestException;
import com.r_n_m.kws.Regulations._exception.FailureException;
import com.r_n_m.kws.Regulations._exception.NotFoundException;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import com.r_n_m.kws.Regulations._models.Forms;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "account", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j(topic = "Controller: Account")
@NoArgsConstructor
public class AccountCtrl extends Assistant {

    @Autowired
    private AccountOps accountOps;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountCtrl(AccountOps accountOps, PasswordEncoder passwordEncoder) {
        this.accountOps = accountOps;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/object-model")
    Account get_model() {
        var account = new Account();
        account.setAccountId(new ObjectId());
        account.setName("John Doe");
        account.setPhone("0716866432");
        account.setRole(Role.GATE);
        account.setUsername("doe");
        //noinspection SpellCheckingInspection
        account.setPassword("{bcrypt}$2a$12$2m3FJIFLG9TMvx3nbmB/W.jvpaMg1YOVcDmKmpvXNDgZoBlLx1hJq");
        account.setAuthorised(true);
        account.setAuthenticated(false);
        return account;
    }

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

        account.setAuthorised(false);
        account.setAuthenticated(false);
        if (accountOps.create_account(account) == null) {
            throw new FailureException("Unable to self register the user");
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    Account update_info(@RequestBody Account account) {
        log.info("body = {}", account);

        if (account.getAccountId() == null) {
            throw new BadRequestException("Please return the provided account ID");
        }

        final var user = accountOps.get_user(account.getAccountId());
        if (user == null) {
            throw new NotFoundException("The target user account could not be found. Maybe it doesn't exist or the account lacks authorization to interact with the system");
        }

        if (account.getName() == null) {
            account.setName(user.getName());
        } else {
            account.setName(StringUtils.capitalize(account.getName()));
        }
        if (account.getPhone() == null) {
            account.setPhone(user.getPhone());
        } else {
            if (!phoneNumber_is_in_correct_format(account.getPhone())) {
                throw new BadRequestException("The phone number provided is not in the correct format");
            }
        }
        if (account.getRole() == null) {
            account.setRole(user.getRole());
        }
        if (account.getUsername() == null) {
            account.setUsername(user.getUsername());
        }
        if (account.getPassword() == null) {
            account.setPassword(user.getPassword());
        } else {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }

        account.setAuthorised(user.isAuthorised());
        account.setAuthenticated(user.isAuthenticated());

        final var updatedAccount = accountOps.update_account(account);
        if (updatedAccount == null) {
            throw new FailureException("User account info has not been updated");
        }

        return updatedAccount;
    }

    @PutMapping(path = "/authorization", consumes = MediaType.APPLICATION_JSON_VALUE)
    void update_authorization(@RequestBody Forms.Auth authForm) {
        log.info("body = {}", authForm);

        if (accountOps.the_username_already_exists(authForm.username())) {
            throw new BadRequestException("The username is already taken, please consider a new one");
        }

        if (!accountOps.update_authorization_status(authForm.username(), authForm.enable())) {
            throw new FailureException("Unable to allow the user to be authorised");
        }
    }

    @PutMapping(path = "/authentication", consumes = MediaType.APPLICATION_JSON_VALUE)
    void update_authentication(@RequestBody Forms.Auth authForm) {
        log.info("body = {}", authForm);

        if (accountOps.the_username_already_exists(authForm.username())) {
            throw new BadRequestException("The username is already taken, please consider a new one");
        }

        if (!accountOps.update_authentication_status(authForm.username(), authForm.enable())) {
            throw new FailureException("Unable to allow the user to be authenticated");
        }
    }

    @DeleteMapping
    Account delete_user(@RequestParam @NonNull String username) {
        final var account = accountOps.get_user(username);
        if (account == null) {
            throw new BadRequestException("The username is already taken, please consider a new one");
        }

        if (!accountOps.delete_account(username)) {
            throw new FailureException("Unable to delete the target user account");
        }

        return account;
    }

    @GetMapping
    List<Account> get_users() {
        return accountOps.get_accounts();
    }

    @GetMapping(params = "q")
    List<Account> get_users(@RequestParam(defaultValue = "") String q, HttpServletResponse httpResponse) {
        if (q.isBlank()) {
            try {
                log.info("Redirecting search to all users");
                httpResponse.sendRedirect("/");
            } catch (IOException e) {
                throw new BadRequestException("No search param provided");
            }
        }

        return accountOps.get_accounts(q);
    }

    @GetMapping("/suggestions")
    Set<String> search_suggestions() {
        return accountOps.get_account_search_suggestions();
    }

}
