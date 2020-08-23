package com.stulsoft.pmongo.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StarterApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StarterApplication.class);

    private final Service1 service1;

    public StarterApplication(@Autowired final Service1 service1) {
        this.service1 = service1;
    }

    public static void main(String[] args) {
        logger.info("==>main");
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "dev");
        SpringApplication.run(StarterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("==>run");
        service1.showDocuments();
    }
}
