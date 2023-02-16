package com.r_n_m.kws.Regulations._services;

import com.mongodb.client.model.Filters;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.0
 * @since 14/02/2023
 */

@Slf4j
@Service
public class Brain implements AccountOps {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var account = get_account(username);
        if (account == null) {
            throw new UsernameNotFoundException("User '%s' could not be found!".formatted(username));
        }
        return new User(account.getUsername(), account.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(account.getRole().getAlias())));
    }

    @Override
    public Account get_account(String username) {
        try {
            var query = new Query();
            query.addCriteria(Criteria.where("username").is(username).and("isAuthenticated").is(true).and("isAuthorised").is(true));
            return mongoTemplate.findOne(query, Account.class);
        } catch (Exception e) {
            log.error("Failed to get user", e);
        }
        return null;
    }

    /**
     * This obtains account information of a user even if its disabled.
     *
     * @param username account username
     * @return null if account is not found
     */
    @Override
    public Account get_user(String username) {
        try {
            var query = new Query();
            query.addCriteria(Criteria.where("username").is(username));
            return mongoTemplate.findOne(query, Account.class);
        } catch (Exception e) {
            log.error("Failed to get user", e);
        }
        return null;
    }

    @Override
    public Account create_account(Account account) {
        try {
            return mongoTemplate.save(account);
        } catch (Exception e) {
            log.error("Failed to create account", e);
        }
        return null;
    }

    /**
     * @param targetAccount The updated state of the target account.
     * @return Null if the change was not successful
     */
    @Override
    public Account update_account(Account targetAccount) {
        return null;
    }

    @Override
    public Boolean delete_account(String username) {
        return null;
    }

    @Override
    public Boolean the_username_already_exists(String username) {
        return get_user(username) != null;
    }

    @Override
    public Boolean the_password_has_been_updated(String newPassword, String username) {
        return null;
    }

    @Override
    public List<Account> get_accounts() {
        try {
            return mongoTemplate.findAll(Account.class);
        } catch (Exception e) {
            log.error("Failed to get accounts", e);
        }
        return null;
    }

    @Override
    public List<Account> get_accounts(String param) {
        try {
            var query = new Query();
            query.addCriteria(Criteria.where("name").regex(".*%s.*".formatted(param)).orOperator(Criteria.where("phone").regex(".*%s.*".formatted(param))).orOperator(Criteria.where("username").regex(".*%s.*".formatted(param))));
            return mongoTemplate.find(query, Account.class);
        } catch (Exception e) {
           log.error("Failed to get accounts based on param", e);
        }
        return null;
    }

    @Override
    public Set<String> get_account_search_suggestions() {
        Set<String> stringSet = new HashSet<>();
        try {
            Bson filter = new Document();
            Bson project = Filters.and(Filters.eq("_id", 0L), Filters.eq("username", 1L), Filters.eq("name", 1L), Filters.eq("phone", 1L));
            mongoTemplate.getCollection(Account.collection).find(filter).projection(project)
                    .forEach(document -> stringSet.addAll(Stream.of(document.get("username"), document.get("name"), document.get("phone")).map(Object::toString).collect(Collectors.toSet())));
        } catch (Exception e) {
            log.error("Failed to get accounts search suggestions", e);
        }
        return stringSet;
    }

    @Override
    public Boolean update_authorization_status(String username, boolean authorize) {
        return null;
    }

    @Override
    public Boolean update_authentication_status(String username, boolean authenticate) {
        return null;
    }
}
