package com.stulsoft.pmongo.spring.latest;

import java.util.Date;

public record Report(
        String _id,
        String name,
        Date date,
        String reportType) {
}
