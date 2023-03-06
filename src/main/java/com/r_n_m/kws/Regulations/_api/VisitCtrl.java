package com.r_n_m.kws.Regulations._api;

import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._entities.Visit;
import com.r_n_m.kws.Regulations._enum.Role;
import com.r_n_m.kws.Regulations._enum.Session;
import com.r_n_m.kws.Regulations._exception.BadRequestException;
import com.r_n_m.kws.Regulations._exception.FailureException;
import com.r_n_m.kws.Regulations._exception.NotFoundException;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import com.r_n_m.kws.Regulations._interface.VisitOps;
import com.r_n_m.kws.Regulations._util.DateUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(path = "visit", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j(topic = "Controller: Visit")
@NoArgsConstructor
public class VisitCtrl extends Assistant {

    @Autowired
    private VisitOps visitOps;

    @Autowired
    private AccountOps accountOps;

    @GetMapping("/object-model")
    Visit get_model() {
        var visit = new Visit();
        visit.setVisitId(UUID.randomUUID());
        visit.setTimestamp(Timestamp.from(Instant.now(clock)));
        visit.setSession(Session.FULL_DAY);
        visit.setPlates("KDD 001Q");
        visit.setPassengers(2);
        visit.setPhone("072797754");

        var account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setName("John Doe");
        account.setPhone("0716866432");
        account.setRole(Role.GATE);
        account.setUsername("doe");
        visit.setAccount(account);

        return visit;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    void save(@RequestBody Visit visit) {
        log.info("body = {}", visit);

        final var warning = visit.get_warning();
        if (warning != null) {
            throw new BadRequestException(warning);
        }

        var account = accountOps.get_account(visit.getAccount().getUsername());
        if (account == null) {
            throw new NotFoundException("The target user account could not be found. Maybe it doesn't exist or the account lacks authorization to interact with the system");
        }
        if (account.getRole() != Role.GATE) {
            throw new BadRequestException("The target account is not authorised to perform this action");
        }
        if (!Objects.equals(account.getName(), visit.getAccount().getName()) || !Objects.equals(account.getPhone(), visit.getAccount().getPhone()) || account.getRole() != visit.getAccount().getRole()) {
            throw new BadRequestException("The user account information does not match those provided, kindly ensure they match and retry");
        }

        if (visitOps.create_visit(visit) == null) {
            throw new FailureException("Failed to record the provided visit");
        }
    }


    @GetMapping
    List<Visit> get_list(@RequestParam(required = false, defaultValue = "") String session, @RequestParam(required = false, defaultValue = "") String param) {
        if (!session.isBlank()) {
            var selectedSession = Arrays.stream(Session.values()).filter(session1 -> session1.name().equals(session)).findFirst().orElse(null);
            if (selectedSession != null) {
                return visitOps.get_visits(selectedSession);
            }
        } else if (!param.isBlank()) {
            return visitOps.get_visits(param);
        }

        return visitOps.get_visits();
    }

    @GetMapping("/timeline")
    List<Visit> get_list_within_timeline(@RequestParam String from, @RequestParam(required = false, defaultValue = "") String to) {
        if (the_date_format_is_NOT_acceptable(from)) {
            throw new BadRequestException("The \"from\" date is not in the correct format");
        }
        if (to.isBlank()) {
            to = get_date();
        } else {
            if (the_date_format_is_NOT_acceptable(to)) {
                throw new BadRequestException("The \"to\" date is not in the correct format");
            }
        }

        final List<Visit> visitList;
        try {
            var beginLocalDate = LocalDate.parse(from, dateTimeFormatter);
            var endLocalDate = LocalDate.parse(to, dateTimeFormatter);
            log.info("from = {}, to = {}", from, to);
            visitList = new ArrayList<>(visitOps.get_visits(DateUtils.asDate(beginLocalDate.atStartOfDay()), DateUtils.asDate(endLocalDate.atTime(23, 59))));
        } catch (Exception e) {
            throw new BadRequestException("The dates provided seem to conflict with the system logic", e);
        }

        return visitList;
    }

}
