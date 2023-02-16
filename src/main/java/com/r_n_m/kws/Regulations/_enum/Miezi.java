/*
 * Copyright (c) Puum Core 2022.
 */

package com.r_n_m.kws.Regulations._enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Puum Core<br>
 * <a href = https://github.com/puumCore>GitHub: Mandela Murithi</a>
 * @version 1.3
 * @since 12/11/2021
 */

@Getter
@RequiredArgsConstructor
public enum Miezi {

    JAN("Jan", "January", 0),
    FEB("Feb", "February", 1),
    MAR("Mar", "March", 2),
    APR("Apr", "April", 3),
    MAY("May", "May", 4),
    JUN("Jun", "June", 5),
    JUL("Jul", "July", 6),
    AUG("Aug", "August", 7),
    SEP("Sep", "September", 8),
    OCT("Oct", "October", 9),
    NOV("Nov", "November", 10),
    DEC("Dec", "December", 11);

    private final String shortForm;
    private final String longForm;
    private final int index;

}
