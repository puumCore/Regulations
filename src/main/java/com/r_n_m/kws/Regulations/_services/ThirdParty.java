package com.r_n_m.kws.Regulations._services;

import com.google.gson.GsonBuilder;
import com.r_n_m.kws.Regulations._custom.Assistant;
import com.r_n_m.kws.Regulations._custom.SmsConfig;
import com.r_n_m.kws.Regulations._exception.BadRequestException;
import com.r_n_m.kws.Regulations._interface.SmsOps;
import com.r_n_m.kws.Regulations._models.MJsonMessageValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j(topic = "3rd Party")
@RequiredArgsConstructor
public class ThirdParty extends Assistant implements SmsOps {

    private MJsonMessageValue executeRequest(List<String> recipients, String message) throws Exception {
        val okHttpClient = new OkHttpClient();
        val to = recipients.stream().map(r -> r + ",").collect(Collectors.joining());

        val formBody = new FormBody.Builder()
                .add("username", SmsConfig.APP_USERNAME.getValue())
                .add("to", to)
                .add("from", SmsConfig.APP.getValue())
                .add("message", message)
                .add("enqueue", "1")
                .build();

        val headers = new Headers.Builder()
                .add("Accept", "application/json")
                .add("Content-Type", "application/x-www-form-urlencoded")
                .add("apiKey", SmsConfig.API_KEY.getValue())
                .build();

        val request = new Request.Builder()
                .url(SmsConfig.BASE_URL.getValue())
                .headers(headers)
                .post(formBody)
                .build();

        val call = okHttpClient.newCall(request);
        try (var response = call.execute()) {
            if (response.body() != null) {
                val body = Objects.requireNonNull(response.body()).string();
                log.info("SMS Response:\n#Message {}\n#Body {}", response.message(), body);
                if (!response.isSuccessful()) {
                    throw new BadRequestException(String.format("Error(%d): %s", response.code(), response.message()));
                }
                if (Stream.of("rejected by gateway", "supplied authentication", "risk hold error", "Insufficient balance").anyMatch(s -> body.toLowerCase().contains(s.toLowerCase()))) {
                    log.error("SMS Failed to send due to '{}'", body);
                    return null;
                }
                return new GsonBuilder().setPrettyPrinting().create().fromJson(body, MJsonMessageValue.class);
            }
            return null;
        }
    }

    @Override
    public Boolean send_message(String phone, String message) {
        var isOkay = false;
        try {
            if (executeRequest(Collections.singletonList(phone), message) != null) {
                isOkay = true;
            }
        } catch (Exception e) {
            log.error("ThirdParty.send_message", e);
        }
        return isOkay;
    }

}
