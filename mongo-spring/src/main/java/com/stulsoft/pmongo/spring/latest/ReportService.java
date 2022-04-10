package com.stulsoft.pmongo.spring.latest;

import com.mongodb.client.MongoClient;
import com.mongodb.internal.operation.AggregateOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final MongoClient client;

    public ReportService(@Autowired @Qualifier("AppMongoClient") MongoClient client) {
        this.client = client;
    }

    public void demoFindLastDocument() {
        MongoOperations mongoOperations = new MongoTemplate(client, "pmongo");

        try {
            // Clear data
            mongoOperations.remove(new Query(), "reports").getDeletedCount();

            var reports = new ArrayList<Report>();
            var now = new Date();
            for (String reportType : Arrays.asList("daily", "monthly")) {
                for (int i = 1; i <= 10; ++i) {
                    reports.add(new Report(null, "name " + i, new Date(now.getTime() + i * 1000), reportType));
                }
            }
            mongoOperations.insert(reports, "reports");

            // Find the last report
            var query = new Query();
            query
                    .addCriteria(Criteria.where("reportType").is("daily"))
                    .with(Sort.by(Sort.Direction.DESC, "date"))
                    .allowDiskUse(true);
            var report = mongoOperations.findOne(query, Report.class, "reports");
            logger.info("{}", report);
            assert ((report != null) && ("name 10".equals(report.name())));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

    public void demoShowAllLastDocuments() {
        MongoOperations mongoOperations = new MongoTemplate(client, "pmongo");
        try {
            var aggregation = newAggregation(
                    sort(Sort.Direction.DESC, "reportType", "date"),
                    group("reportType").first("$$ROOT").as("entry"),
                    replaceRoot("entry")
            );

            var result = mongoOperations.aggregate(aggregation, "reports", Report.class);
            result.getMappedResults().forEach(report -> logger.info("{}", report));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}
