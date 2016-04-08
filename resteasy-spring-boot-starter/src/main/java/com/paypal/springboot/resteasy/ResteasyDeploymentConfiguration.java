package com.paypal.springboot.resteasy;

import java.util.Set;

import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;

/**
 * Creates a deployment from {@link ResteasyProperties#getInit()} String-based configuration data.
 */
public class ResteasyDeploymentConfiguration extends ConfigurationBootstrap {
    private final ResteasyProperties properties;

    public ResteasyDeploymentConfiguration(ResteasyProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getParameter(String name) {
        return getInitParameter(name);
    }

    @Override
    public Set<String> getParameterNames() {
        return getInitParameterNames();
    }

    @Override
    public String getInitParameter(String name) {
        return properties.getInit().get(name);
    }

    @Override
    public Set<String> getInitParameterNames() {
        return properties.getInit().keySet();
    }

    public ResteasyProperties getResteasyProperties() {
        return properties;
    }
}
