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
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
        log.error(super.getMessage());
    }

    public NotFoundException(String message, Object... args) {
        super(String.format(message, args));
        log.error(super.getMessage());
    }

}
