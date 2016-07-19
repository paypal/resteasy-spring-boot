package com.paypal.springboot.resteasy;

import org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is a Spring version of {@link ResteasyServletInitializer}.
 * It does not register the servlets though, that is done by {@link ResteasyApplicationBuilder}
 * It only finds the JAX-RS Application classes (by scanning the classpath), and
 * the JAX-RS Path and Provider annotated Spring beans, and then register the
 * Spring bean definitions that represent each servlet registration.
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class ResteasyEmbeddedServletInitializer implements BeanFactoryPostProcessor {

    private static final String JAXRS_APP_CLASSES_PROPERTY = "resteasy.jaxrs.app";
    private static final String JAXRS_APP_CLASSES_DEFINITION_PROPERTY = "resteasy.jaxrs.app.registration";

    private Set<Class<? extends Application>> applications;
    private Set<Class<?>> allResources;
    private Set<Class<?>> providers;

    private static final Logger logger = LoggerFactory.getLogger(ResteasyEmbeddedServletInitializer.class);

    private enum JaxrsAppClassesRegistration {
        BEANS, PROPERTY, SCANNING, AUTO
    }

    /**
     * Find the JAX-RS application classes.
     * This is done by one of these three options in this order:
     *
     * 1- By having them defined as Spring beans
     * 2- By setting property {@code resteasy.jaxrs.app} via Spring Boot application properties file.
     *    This property should contain a comma separated list of JAX-RS sub-classes
     * 3- Via classpath scanning (looking for javax.ws.rs.core.Application sub-classes)
     *
     * First try to find JAX-RS Application sub-classes defined as Spring beans. If that is existent,
     * the search stops, and those are the only JAX-RS applications to be registered.
     * If no JAX-RS application Spring beans are found, then see if Sring Boot property {@code resteasy.jaxrs.app}
     * has been set. If it has, the search stops, and those are the only JAX-RS applications to be registered.
     * If not, then scan the classpath searching for JAX-RS applications.
     *
     * There is a way though to force one of the options above, which is by setting property
     * {@code resteasy.jaxrs.app.registration} via Spring Boot application properties file. The possible valid
     * values are {@code beans}, {@code property}, {@code scanning} or {@code auto}. If this property is not
     * present, the default value is {@code auto}, which means every approach will be tried in the order and way
     * explained earlier.
     *
     * @param beanFactory
     */
    private void findJaxrsApplications(ConfigurableListableBeanFactory beanFactory) {
        logger.info("Finding JAX-RS Application classes");

        JaxrsAppClassesRegistration registration = getJaxrsAppClassesRegistration(beanFactory);

        switch (registration) {
            case AUTO:
                applications = findJaxrsApplicationBeans(beanFactory);
                if(applications == null) applications = findJaxrsApplicationProperty(beanFactory);
                if(applications == null) applications = findJaxrsApplicationScanning();
                break;
            case BEANS:
                applications = findJaxrsApplicationBeans(beanFactory);
                break;
            case PROPERTY:
                applications = findJaxrsApplicationProperty(beanFactory);
                break;
            case SCANNING:
                applications = findJaxrsApplicationScanning();
                break;
        }

        if(applications != null) {
            for (Object appClass : applications.toArray()) {
                logger.info("JAX-RS Application class found: {}", ((Class<Application>) appClass).getName());
            }
        }
    }

    private JaxrsAppClassesRegistration getJaxrsAppClassesRegistration(ConfigurableListableBeanFactory beanFactory) {
        ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        String jaxrsAppClassesRegistration = configurableEnvironment.getProperty(JAXRS_APP_CLASSES_DEFINITION_PROPERTY);
        JaxrsAppClassesRegistration registration = JaxrsAppClassesRegistration.AUTO;

        if(jaxrsAppClassesRegistration == null) {
            logger.info("Property {} has not been set, JAX-RS Application classes registration is being set to AUTO", JAXRS_APP_CLASSES_DEFINITION_PROPERTY);
        } else {
            logger.info("Property {} has been set to {}", JAXRS_APP_CLASSES_DEFINITION_PROPERTY, jaxrsAppClassesRegistration);
            try {
                registration = JaxrsAppClassesRegistration.valueOf(jaxrsAppClassesRegistration.toUpperCase());
            } catch(IllegalArgumentException ex) {
                String errorMesage = String.format("Property %s has not been properly set, value %s is invalid. JAX-RS Application classes registration is being set to AUTO.", JAXRS_APP_CLASSES_DEFINITION_PROPERTY, jaxrsAppClassesRegistration);
                logger.error(errorMesage);
                throw new IllegalArgumentException(errorMesage, ex);
            }
        }

        return registration;
    }

    /**
     * Find JAX-RS application classes by searching for their related
     * Spring beans. If there is none, then return null.
     *
     * @return the set of JAX-RS application classes found
     * @param beanFactory
     */
    private Set<Class<? extends Application>> findJaxrsApplicationBeans(ConfigurableListableBeanFactory beanFactory) {
        logger.info("Searching for JAX-RS Application Spring beans");

        Map<String, Application> applicationBeans = beanFactory.getBeansOfType(Application.class, true, false);
        if(applicationBeans == null || applicationBeans.size() == 0) {
            logger.info("No JAX-RS Application Spring beans found");
            return null;
        }

        Set<Class<? extends Application>> applications = new HashSet<Class<? extends Application>>();
        for (Application application : applicationBeans.values()) {
            applications.add(application.getClass());
        }

        return applications;
    }

    /**
     * Find JAX-RS application classes via property {@code resteasy.jaxrs.app}.
     * If that has not been set, then return null
     *
     * @return the set of JAX-RS application classes found
     */
    private Set<Class<? extends Application>> findJaxrsApplicationProperty(ConfigurableListableBeanFactory beanFactory) {
        ConfigurableEnvironment configurableEnvironment = beanFactory.getBean(ConfigurableEnvironment.class);
        String jaxrsAppsProperty = configurableEnvironment.getProperty(JAXRS_APP_CLASSES_PROPERTY);
        if(jaxrsAppsProperty == null) {
            logger.info("No JAX-RS Application set via property {}", JAXRS_APP_CLASSES_PROPERTY);
            return null;
        } else {
            logger.info("Property {} has been set", JAXRS_APP_CLASSES_PROPERTY);
        }

        String[] jaxrsClassNames = jaxrsAppsProperty.split(",");
        Set<Class<? extends Application>> applications = new HashSet<Class<? extends Application>>();

        for(String jaxrsClassName : jaxrsClassNames) {
            Class<? extends Application> jaxrsClass = null;
            try {
                jaxrsClass = (Class<? extends Application>) Class.forName(jaxrsClassName.trim());
            } catch (ClassNotFoundException e) {
                String exceptionMessage = String.format("JAX-RS Application class %s has not been found", jaxrsClassName.trim());
                logger.error(exceptionMessage, e);
                throw new BeansException(exceptionMessage, e){};
            }
            applications.add(jaxrsClass);
        }

        return applications;
    }

    /**
     * Find JAX-RS application classes by scanning the class-path.
     *
     * @return the set of JAX-RS application classes found
     */
    private Set<Class<? extends Application>> findJaxrsApplicationScanning() {
        return JaxrsApplicationScanner.getApplications();
    }

    /**
     * Search for JAX-RS resource and provider Spring beans,
     * which are the ones whose classes are annotated with
     * {@link Path} or {@link Provider} respectively
     *
     * @param beanFactory
     */
    private void findJaxrsResourcesAndProviderClasses(ConfigurableListableBeanFactory beanFactory) {
        logger.debug("Finding JAX-RS resources and providers Spring bean classes");

        String[] resourceBeans = beanFactory.getBeanNamesForAnnotation(Path.class);
        String[] providerBeans = beanFactory.getBeanNamesForAnnotation(Provider.class);

        allResources = new HashSet<Class<?>>();
        providers = new HashSet<Class<?>>();

        if(resourceBeans != null) {
            for(String resourceBean : resourceBeans) {
                allResources.add(beanFactory.getType(resourceBean));
            }
        }

        if(providerBeans != null) {
            for(String providerBean : providerBeans) {
                providers.add(beanFactory.getType(providerBean));
            }
        }

        if(logger.isDebugEnabled()) {
            for (Object resourceClass : allResources.toArray()) {
                logger.debug("JAX-RS resource class found: {}", ((Class) resourceClass).getName());
            }
        }
        if(logger.isDebugEnabled()) {
            for (Object providerClass: providers.toArray()) {
                logger.debug("JAX-RS provider class found: {}", ((Class) providerClass).getName());
            }
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.debug("Post process bean factory has been called");

        findJaxrsApplications(beanFactory);

        // This is done by finding their related Spring beans
        findJaxrsResourcesAndProviderClasses(beanFactory);

        if (applications == null || applications.size() == 0) {
            logger.warn("No JAX-RS Application classes have been found");
            return;
        }
        if (allResources == null || allResources.size() == 0) {
            logger.warn("No JAX-RS resource Spring beans have been found");
            return;
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        for (Class<? extends Application> applicationClass : applications) {
            ApplicationPath path = applicationClass.getAnnotation(ApplicationPath.class);
            if (path == null) {
                logger.warn("JAX-RS Application class {} has no ApplicationPath annotation, so it will not be registered", applicationClass.getName());
                continue;
            }

            logger.debug("registering JAX-RS application class " + applicationClass.getName());

            GenericBeanDefinition applicationServletBean = createApplicationServlet(applicationClass, path.value());
            registry.registerBeanDefinition(applicationClass.getName(), applicationServletBean);
        }

    }

    /**
     * Creates a Servlet bean definition for the given JAX-RS application
     *
     * @param applicationClass
     * @param path
     * @return a Servlet bean definition for the given JAX-RS application
     */
    private GenericBeanDefinition createApplicationServlet(Class<? extends Application> applicationClass, String path) {
        GenericBeanDefinition applicationServletBean = new GenericBeanDefinition();
        applicationServletBean.setFactoryBeanName(ResteasyApplicationBuilder.BEAN_NAME);
        applicationServletBean.setFactoryMethodName("build");

        Set<Class<?>> resources = allResources;

        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addIndexedArgumentValue(0, applicationClass.getName());
        values.addIndexedArgumentValue(1, path);
        values.addIndexedArgumentValue(2, resources);
        values.addIndexedArgumentValue(3, providers);
        applicationServletBean.setConstructorArgumentValues(values);

        applicationServletBean.setAutowireCandidate(false);
        applicationServletBean.setScope("singleton");

        return applicationServletBean;
    }

}
