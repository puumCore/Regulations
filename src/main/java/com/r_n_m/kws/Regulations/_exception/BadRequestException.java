package com.r_n_m.kws.Regulations._exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 19/07/2022
 */

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
        log.error(super.getMessage());
    }

    public BadRequestException(String message, Object... args) {
        super(String.format(message, args));
        log.error(super.getMessage());
    }
}