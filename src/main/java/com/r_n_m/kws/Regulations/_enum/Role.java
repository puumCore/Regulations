package com.r_n_m.kws.Regulations._enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Puum Core (Mandela Murithi)<br>
 * <a href = "https://github.com/puumCore">GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 18/07/2022
 */

@Getter
@RequiredArgsConstructor
public enum Role {

    GATE("Gate Ranger"),
    PATROL("Patrol Ranger"),
    ADMIN("Administrator");

    private final String alias;

}
