/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import java.text.NumberFormat;

/**
 * @author Yuriy Stul
 */
public class StopWatch {
    private final long start;

    public StopWatch() {
        start = System.currentTimeMillis();
    }

    public String duration() {
        var format = NumberFormat.getIntegerInstance();
        return String.format("Duration is %s ms", format.format(System.currentTimeMillis() - start));
    }
}
