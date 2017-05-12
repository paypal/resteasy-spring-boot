package com.test.multicontexttest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * SpringBoot entry point application
 *
 * Created by facarvalho on 12/7/15.
 */
@SpringBootApplication
public class MultiContextTestApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MultiContextTestApp.class, args);
    }

}
