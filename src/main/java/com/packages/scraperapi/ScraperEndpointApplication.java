package com.packages.scraperapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;

@SpringBootApplication(
        exclude = {
                org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration.class,
                HttpClientAutoConfiguration.class
        }
)
public class ScraperEndpointApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScraperEndpointApplication.class, args);
        System.out.println("Running Java version: " + System.getProperty("java.version"));

    }

}
