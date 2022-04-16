/*
 * Copyright (c) StulSoft, 2022
 */

package com.stulsoft.pmongo.manipulations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class MongoSpringManipulationsApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MongoSpringManipulationsApplication.class);

    public static void main(String[] args) {
        logger.info("==>main");
        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "dev");
        SpringApplication.run(MongoSpringManipulationsApplication.class, args);
    }

    @Override
    public void run(String... args){
        try {
            var url = "http://localhost:8080";
            var runTime = Runtime.getRuntime();
            runTime.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}
