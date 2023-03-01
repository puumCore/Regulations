package com.r_n_m.kws.Regulations._services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import com.r_n_m.kws.Regulations._models.Forms;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.0
 * @since 14/02/2023
 */

@Service
@Slf4j
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
            query.addCriteria(
                    Criteria
                            .where("username").is(username)
                            .and("isAuthenticated").is(true)
                            .and("isAuthorised").is(true)
            );
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
    public Account get_user(UUID uuid) {
        try {
            return mongoTemplate.findById(uuid, Account.class);
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
        try {
            var query = new Query();
            query.addCriteria(Criteria.where("_id").is(targetAccount.getAccountId()));
            return mongoTemplate.findAndReplace(query, targetAccount);
        } catch (Exception e) {
            log.error("Failed to update account info", e);
        }
        return null;
    }

    @Override
    public boolean delete_account(String username) {
        try {
            var query = new Query();
            query.addCriteria(Criteria.where("username").is(username));
            var criteria = new Criteria();
            criteria.orOperator(
                    Criteria.where("isAuthenticated").is(true),
                    Criteria.where("isAuthorised").is(true)
            );
            query.addCriteria(criteria);
            final var deletedCount = mongoTemplate.remove(query, Account.class).getDeletedCount();
            log.info("Deleted '{}' number of users with the username = '{}'", deletedCount, username);
            return deletedCount > 0;
        } catch (Exception e) {
            log.error("Failed to delete account", e);
        }
        return false;
    }

    @Override
    public Boolean the_username_already_exists(String username) {
        return get_user(username) != null;
    }

    @Override
    public Boolean the_password_has_been_updated(String username, String newPassword) {
        try {
            var modifiedCount = mongoTemplate
                    .getCollection(Account.collection)
                    .updateOne(Filters.eq("username", username), Updates.combine(Updates.set("password", newPassword)))
                    .getModifiedCount();
            log.info("Updated '{}' number of users with the username = '{}'", modifiedCount, username);
            return modifiedCount > 0;
        } catch (Exception e) {
            log.error("Failed to update authorization account", e);
        }
        return false;
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
            var criteria = new Criteria();
            var containsPattern = Pattern.compile("%s(?i)".formatted(param));
            criteria.orOperator(
                    Criteria.where("name").regex(containsPattern),
                    Criteria.where("phone").regex(containsPattern),
                    Criteria.where("role").regex(containsPattern),
                    Criteria.where("username").regex(containsPattern)
            );
            return mongoTemplate.find(new Query(criteria), Account.class);
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
            Bson project = new Document("_id", 0L)
                    .append("name", 1L)
                    .append("phone", 1L)
                    .append("username", 1L);
            var documents = mongoTemplate
                    .getCollection(Account.collection)
                    .find(filter)
                    .projection(project);
            documents.forEach(doc -> {
                var jsonElement = JsonParser.parseString(doc.toJson());
                var jsonObject = jsonElement.getAsJsonObject();
                jsonObject
                        .asMap()
                        .values()
                        .stream()
                        .map(JsonElement::getAsString)
                        .collect(Collectors.toCollection(() -> stringSet));
            });
        } catch (Exception e) {
            log.error("Failed to get accounts search suggestions", e);
        }
        return stringSet;
    }

    @Override
    public Boolean update_authorization_status(Forms.Auth auth) {
        try {
            var modifiedCount = mongoTemplate
                    .getCollection(Account.collection)
                    .updateOne(Filters.eq("username", auth.username()), Updates.combine(Updates.set("isAuthorised", auth.enable())))
                    .getModifiedCount();
            log.info("Updated '{}' number of users with the username = '{}'", modifiedCount, auth.username());
            return modifiedCount > 0;
        } catch (Exception e) {
            log.error("Failed to update authorization account", e);
        }
        return false;
    }

    @Override
    public Boolean update_authentication_status(Forms.Auth auth) {
        try {
            var modifiedCount = mongoTemplate
                    .getCollection(Account.collection)
                    .updateOne(Filters.eq("username", auth.username()), Updates.combine(Updates.set("isAuthenticated", auth.enable())))
                    .getModifiedCount();
            log.info("Updated '{}' number of users with the username = '{}'", modifiedCount, auth.username());
            return modifiedCount > 0;
        } catch (Exception e) {
            log.error("Failed to update authentication account", e);
        }
        return false;
    }

}
