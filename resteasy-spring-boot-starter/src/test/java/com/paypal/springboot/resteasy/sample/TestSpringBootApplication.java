package com.paypal.springboot.resteasy.sample;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.paypal.springboot.resteasy.sample",
    includeFilters = {
        @ComponentScan.Filter(Path.class),
        @ComponentScan.Filter(Provider.class),
        @ComponentScan.Filter(ApplicationPath.class)
    })
public class TestSpringBootApplication {
    
}
