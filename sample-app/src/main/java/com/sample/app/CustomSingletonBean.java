package com.sample.app;

import org.springframework.stereotype.Component;

/**
 * This singleton Spring bean just exists to
 * test that injections work normally in
 * JAX-RS provider beans
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component
public class CustomSingletonBean {

    public boolean amIAlive() {
        return true;
    }

}
