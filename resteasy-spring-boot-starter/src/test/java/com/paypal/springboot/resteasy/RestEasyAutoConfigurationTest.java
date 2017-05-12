package com.paypal.springboot.resteasy;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.mock.web.MockServletContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by facarvalho on 11/24/15.
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class RestEasyAutoConfigurationTest {

    @Test
    public void springBeanProcessor() {
        BeanFactoryPostProcessor beanFactoryPostProcessor = new RestEasyAutoConfiguration().springBeanProcessor();

        Assert.assertNotNull(beanFactoryPostProcessor);
        Assert.assertEquals(SpringBeanProcessor.class, beanFactoryPostProcessor.getClass());

        SpringBeanProcessor springBeanProcessor = (SpringBeanProcessor) beanFactoryPostProcessor;
        Registry springBeanProcessorRegistry = springBeanProcessor.getRegistry();
        ResteasyProviderFactory providerFactory = springBeanProcessor.getProviderFactory();

        Assert.assertNotNull(springBeanProcessorRegistry);
        Assert.assertNotNull(providerFactory);
        Assert.assertEquals(ResourceMethodRegistry.class, springBeanProcessorRegistry.getClass());
    }

    @Test
    public void resteasyBootstrapListener() {
        RestEasyAutoConfiguration restEasyAutoConfiguration = new RestEasyAutoConfiguration();
        BeanFactoryPostProcessor beanFactoryPostProcessor = RestEasyAutoConfiguration.springBeanProcessor();
        ServletContextListener servletContextListener = restEasyAutoConfiguration.resteasyBootstrapListener(beanFactoryPostProcessor);
        Assert.assertNotNull(servletContextListener);

        ServletContext servletContext = new MockServletContext();

        ServletContextEvent sce = new ServletContextEvent(servletContext);
        servletContextListener.contextInitialized(sce);

        ResteasyProviderFactory servletContextProviderFactory = (ResteasyProviderFactory) servletContext.getAttribute(ResteasyProviderFactory.class.getName());
        Dispatcher servletContextDispatcher = (Dispatcher) servletContext.getAttribute(Dispatcher.class.getName());
        Registry servletContextRegistry = (Registry) servletContext.getAttribute(Registry.class.getName());

        Assert.assertNotNull(servletContextProviderFactory);
        Assert.assertNotNull(servletContextDispatcher);
        Assert.assertNotNull(servletContextRegistry);

        // Exercising fully cobertura branch coverage
        servletContextListener.contextDestroyed(sce);
        ServletContextListener servletContextListener2 = restEasyAutoConfiguration.resteasyBootstrapListener(beanFactoryPostProcessor);
        servletContextListener2.contextDestroyed(sce);
    }

}
