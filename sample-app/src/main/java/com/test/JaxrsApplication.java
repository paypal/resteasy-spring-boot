package com.test;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.stereotype.Component;

/**
 * JAX-RS application
 *
 * Created by facarvalho on 12/7/15.
 */
@ApplicationPath("/sample-app/")
@Component
public class JaxrsApplication extends Application {
}
