package com.r_n_m.kws.Regulations._api;

import com.google.gson.GsonBuilder;
import com.r_n_m.kws.Regulations._entities.Account;
import com.r_n_m.kws.Regulations._enum.Role;
import com.r_n_m.kws.Regulations._interface.AccountOps;
import jakarta.annotation.security.RunAs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@RunAs(value = "Administrator")
class AccountCtrlTest {

    private MockMvc mockMvc;
    @Autowired
    private AccountOps accountOps;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccountCtrl(accountOps, passwordEncoder))
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    void get_model() throws Exception {
        this.mockMvc.perform(
                        get("/account/object-model")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.phone").value("0716866432"))
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.username").value("doe"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void self_registration() throws Exception {
        var account = new Account();
        account.setName("Simon Bejah");
        account.setPhone("0716866432");
        account.setRole(Role.PATROL);
        account.setUsername("bejah");
        account.setPassword(account.getUsername());

        this.mockMvc.perform(
                        post("/account/self-register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new GsonBuilder().setPrettyPrinting().create().toJson(account))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void update_info() {
    }

    @Test
    void update_authorization() {
    }

    @Test
    void update_authentication() {
    }

    @Test
    void delete() {
    }

    @Test
    void get_users() {
    }

    @Test
    void testGet_users() {
    }

    @Test
    void search_suggestions() {
    }


}