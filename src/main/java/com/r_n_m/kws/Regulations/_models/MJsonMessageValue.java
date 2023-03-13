package com.r_n_m.kws.Regulations._models;

import lombok.Data;

import java.util.List;

@Data
public class MJsonMessageValue {

    private MessageData messageData;

    @Data
    public static class MessageData {

        private String message;
        private List<Recipient> recipients;

    }

    @Data
    public static class Recipient {

        private Integer statusCode;
        private String number;
        private String cost;
        private String status;
        private String messageId;

    }

}
