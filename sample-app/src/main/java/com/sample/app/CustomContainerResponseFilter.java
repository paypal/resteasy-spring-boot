package com.sample.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Custom container request filter just to
 * exercise Spring beans as providers and,
 * specifically, in this case, as a filter.
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component
@Provider
public class CustomContainerResponseFilter implements ContainerResponseFilter {

    @Autowired
    private CustomSingletonBean customSingletonBean;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        // This will cause a NPE if this bean couldn't be injected,
        // and that is all we want to check. No need for assertions here
        customSingletonBean.amIAlive();

        // Checks if request has a HTTP header named "ping".
        // If it does, adds an HTTP header named "pong" to the response.
        // The header value is irrelevant.
        if(requestContext.getHeaderString("ping") != null) {
            responseContext.getHeaders().add("pong", "pong");
        }
    }

}
