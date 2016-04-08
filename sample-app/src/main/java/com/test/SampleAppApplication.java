package com.test;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * SpringBoot entry point application
 *
 * Created by facarvalho on 12/7/15.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.test",
    includeFilters = {
        @ComponentScan.Filter(Path.class),
        @ComponentScan.Filter(Provider.class),
    })
public class SampleAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SampleAppApplication.class, args);
    }
    
}
