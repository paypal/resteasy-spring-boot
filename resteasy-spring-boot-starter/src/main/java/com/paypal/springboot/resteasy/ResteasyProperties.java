package com.paypal.springboot.resteasy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Represents all externalized properties prefixed by "spring.resteasy".
 */
@ConfigurationProperties("spring.resteasy")
public class ResteasyProperties {
    /**
     * Init parameters to pass to RESTEasy ConfigurationBootstrap.
     */
    private Map<String, String> init = new HashMap<String, String>();

    /**
     * Path that serves as the base URI for the application. Overrides the value of
     * "@ApplicationPath" if specified.
     */
    private String applicationPath;

    public Map<String, String> getInit() {
        return init;
    }

    public void setInit(Map<String, String> init) {
        this.init = init;
    }

    public String getApplicationPath() {
        return applicationPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }
}
