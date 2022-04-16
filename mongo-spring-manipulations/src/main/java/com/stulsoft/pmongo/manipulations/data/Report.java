/*
 * Copyright (c) StulSoft, 2022
 */

package com.stulsoft.pmongo.manipulations.data;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reports")
public record Report(
        String _id,
        String name,
        Date date,
        String reportType) {
}
