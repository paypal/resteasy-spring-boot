package com.test;

import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS application
 *
 * Created by facarvalho on 12/7/15.
 */
@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {
}
