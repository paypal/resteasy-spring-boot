package com.paypal.springboot.resteasy.sample;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample test JAX-RS application.
 *
 * Created by facarvalho on 11/25/15.
 */
@ApplicationPath("/myapp1")
public class TestApplication1 extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(TestResource1.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<Object>();
        singletons.add(new TestResource2());
        return singletons;
    }

}
