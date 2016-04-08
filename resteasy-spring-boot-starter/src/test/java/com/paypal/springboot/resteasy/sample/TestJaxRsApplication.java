package com.paypal.springboot.resteasy.sample;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(TestJaxRsApplication.APPLICATION_PATH)
public class TestJaxRsApplication extends Application {
    public static final String APPLICATION_PATH = "/test1";
}
