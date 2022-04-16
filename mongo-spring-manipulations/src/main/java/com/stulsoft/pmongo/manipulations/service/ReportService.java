/*
 * Copyright (c) StulSoft, 2022
 */

package com.stulsoft.pmongo.manipulations.service;

import com.mongodb.client.MongoClient;
import com.stulsoft.pmongo.manipulations.data.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final MongoOperations mongoOperations;

    public ReportService(@Autowired @Qualifier("AppMongoClient") MongoClient client, Environment environment) {
        mongoOperations = new MongoTemplate(client, Objects.requireNonNull(environment.getProperty("data.dbname")));
    }

    public String showAllReports() {
//        var reports = mongoOperations.findAll(Report.class, "reports");
        var reports = mongoOperations.findAll(Report.class);
        return reports.stream().map(Report::toString).collect(Collectors.joining("<br/>"));
    }

    public String showAllLastReport() {
        try {
            AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();
            var aggregation = newAggregation(
                    sort(Sort.Direction.DESC, "reportType", "date"),
                    group("reportType").first("$$ROOT").as("entry"),
                    replaceRoot("entry")
            ).withOptions(options);

//            var result = mongoOperations.aggregate(aggregation, "reports", Report.class);
            var result = mongoOperations.aggregate(aggregation, Report.class, Report.class);
            return result.getMappedResults().stream().map(Report::toString).collect(Collectors.joining("<br/>"));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            return exception.getMessage();
        }
    }

    public void recreateReports() {
        // Clear data
//        mongoOperations.remove(new Query(), "reports");
        mongoOperations.remove(new Query(), Report.class);

        var reports = new ArrayList<Report>();
        var now = new Date();
        for (String reportType : Arrays.asList("daily", "monthly", "yearly")) {
            for (int i = 1; i <= 10; ++i) {
                reports.add(new Report(null, "name " + i, new Date(now.getTime() + i * 1000), reportType));
            }
        }
//        mongoOperations.insert(reports, "reports");
        mongoOperations.insert(reports, Report.class);
    }

    public void updateReport() {
        var query = new Query();
        query.addCriteria(Criteria.where("reportType").is("daily"))
                .with(Sort.by(Sort.Direction.DESC, "date"));
//        var report = mongoOperations.findOne(query, Report.class, "reports");
        var report = mongoOperations.findOne(query, Report.class);

        if (report != null) {
            var reportUpdated = new Report(report._id(), report.name() + " updated", report.date(), report.reportType());
            try {
//                mongoOperations.save(reportUpdated, "reports");
                mongoOperations.save(reportUpdated);
            }catch(Exception exception){
                logger.error("Failed to updated a report: " + exception.getMessage(), exception);
            }
        }
    }
}
