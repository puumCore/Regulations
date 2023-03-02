package com.r_n_m.kws.Regulations._api;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "visit", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j(topic = "Controller: Visit")
@NoArgsConstructor
public class VisitCtrl {
}
