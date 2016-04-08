package com.paypal.springboot.resteasy;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ConfigurationBootstrap;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import static org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX;

@Configuration
@ConditionalOnClass(
    name = {"org.jboss.resteasy.plugins.spring.SpringResourceFactory", "javax.servlet.ServletRegistration"}
)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(DispatcherServletAutoConfiguration.class)
@EnableConfigurationProperties(ResteasyProperties.class)
public class ResteasyAutoConfiguration implements ServletContextAware {

    @Autowired
    protected ResteasyProperties properties;
    @Autowired(required = false)
    protected Application application;

    protected ServletContext servletContext;

    @Bean
    @ConditionalOnMissingBean
    public static ResteasyProviderFactory resteasyProviderFactory() {
        return new ResteasyProviderFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public static CustomSpringBeanProcessor resteasyBeanProcessor(ResteasyProviderFactory providerFactory) {
        return new CustomSpringBeanProcessor(providerFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationBootstrap resteasyDeploymentConfiguration(ResteasyProperties properties) {
        return new ResteasyDeploymentConfiguration(properties);
    }

    @Bean(destroyMethod = "stop")
    @ConditionalOnMissingBean
    public ResteasyDeployment resteasyDeployment(
        ResteasyProviderFactory providerFactory, ConfigurationBootstrap configuration) {

        ResteasyDeployment deployment = configuration.createDeployment();
        deployment.setProviderFactory(providerFactory);
        deployment.start();

        if (servletContext != null) {
            deployment.getDefaultContextObjects().put(ServletContext.class, servletContext);

            servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
            servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
            servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());

            updateServletContextDeployments(configuration, deployment);
        }

        return deployment;
    }

    @Bean
    @ConditionalOnMissingBean(name = "resteasyServletRegistration")
    public ServletRegistrationBean resteasyServletRegistration(ResteasyConfiguration configuration) {
        String path = getApplicationPath();
        ServletRegistrationBean registration = new ServletRegistrationBean(new HttpServlet30Dispatcher(), path + "/*");
        if (application == null) {
            registration.setName("javax.ws.rs.Application");
        } else {
            String applicationName = ClassUtils.getUserClass(application).getName();
            registration.setName(applicationName);
            registration.addInitParameter("javax.ws.rs.Application", applicationName);
        }
        for (String parameter : configuration.getInitParameterNames()) {
            registration.addInitParameter(parameter, configuration.getInitParameter(parameter));
        }
        registration.addInitParameter(RESTEASY_SERVLET_MAPPING_PREFIX, path);
        return registration;
    }

    @Bean
    public Registry resteasyRegistry(ResteasyDeployment deployment) {
        return deployment.getRegistry();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @SuppressWarnings("unchecked")
    private void updateServletContextDeployments(ResteasyConfiguration configuration, ResteasyDeployment deployment) {
        Map<String, ResteasyDeployment> deployments = (Map<String, ResteasyDeployment>)
            servletContext.getAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS);
        if (deployments == null) {
            deployments = new ConcurrentHashMap<String, ResteasyDeployment>();
            servletContext.setAttribute(ResteasyContextParameters.RESTEASY_DEPLOYMENTS, deployments);
        }
        String mappingPrefix = configuration.getParameter(RESTEASY_SERVLET_MAPPING_PREFIX);
        deployments.put(mappingPrefix == null ? "" : mappingPrefix.trim(), deployment);
    }
    
    private String getApplicationPath() {
        if (StringUtils.hasText(properties.getApplicationPath())) {
            return parseApplicationPath(properties.getApplicationPath());
        } else {
            ApplicationPath annotation = AnnotationUtils.findAnnotation(application.getClass(), ApplicationPath.class);
            return annotation == null ? "" : parseApplicationPath(annotation.value());
        }
    }

    private String parseApplicationPath(String prefix) {
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
    }
}

