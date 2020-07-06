package io.github.holovid.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HolovidServerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger("HolovidServer");

    public HolovidServerApplication() {
        LOGGER.info("Hello... and goodbye!");
        System.exit(0);
    }

    public static void main(final String[] args) {
        SpringApplication.run(HolovidServerApplication.class, args);
    }
}
