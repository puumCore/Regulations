package com.r_n_m.kws.Regulations._interface;

import com.r_n_m.kws.Regulations._entities.Account;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 19/07/2022
 */

public interface AccountOps extends UserDetailsService {

    Account get_account(String username);


    /**
     * This obtains account information of a user even if its disabled.
     *
     * @param username account username
     * @return null if account is not found
     */
    Account get_user(String username);

    Account create_account(Account account);

    /**
     * @param targetAccount The updated state of the target account.
     * @return Null if the change was not successful
     */
    Account update_account(Account targetAccount);

    Boolean delete_account(String username);

    Boolean the_username_already_exists(String username);

    Boolean the_password_has_been_updated(String newPassword, String username);

    List<Account> get_accounts();

    List<Account> get_accounts(String param);

    Set<String> get_account_search_suggestions();

    Boolean update_authorization_status(String username, boolean authorize);

    Boolean update_authentication_status(String username, boolean authenticate);

    default String get_password() {
        return RandomStringUtils.randomAlphanumeric(8, 12);
    }

}