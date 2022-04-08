package com.stulsoft.pmongo.spring;

import com.stulsoft.pmongo.spring.latest.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class StarterApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StarterApplication.class);

    private final Service1 service1;
    private final ReportService reportService;

    public StarterApplication(Service1 service1, ReportService reportService) {
        this.service1 = service1;
        this.reportService = reportService;
    }

    public static void main(String[] args) {
        logger.info("==>main");
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "dev");
        SpringApplication.run(StarterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("==>run");
//        service1.showDocuments();
        reportService.demoFindLastDocument();
    }
}
