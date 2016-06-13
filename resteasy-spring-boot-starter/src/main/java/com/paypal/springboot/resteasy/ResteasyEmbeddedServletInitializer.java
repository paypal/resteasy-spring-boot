package com.paypal.springboot.resteasy;

import org.apache.commons.io.FilenameUtils;
import org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a Spring version of {@link ResteasyServletInitializer}
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class ResteasyEmbeddedServletInitializer implements BeanFactoryPostProcessor {

    private Set<Class<? extends Application>> applications;
    private Set<Class<?>> allResources;
    private Set<Class<?>> providers;

    private static final Logger logger = LoggerFactory.getLogger(ResteasyEmbeddedServletInitializer.class);

    /**
     * Copy all entries that are a JAR file or a directory
     */
    private void copyValidClasspathEntries(Collection<URL> source, Set<URL> destination) {
        String fileName;
        boolean isJarFile;
        boolean isDirectory;

        for (URL url : source) {
            if(destination.contains(url)) {
                continue;
            }

            fileName = url.getFile();
            isJarFile = FilenameUtils.isExtension(fileName, "jar");
            isDirectory = new File(fileName).isDirectory();

            if (isJarFile || isDirectory) {
                destination.add(url);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Ignored classpath entry: " + fileName);
            }
        }
    }

    /**
     * Scan the Classpath searching for classes of type {@link Application}
     */
    private void findJaxrsApplicationClasses() {
        logger.debug("Finding JAX-RS Application classes");

        final Collection<URL> systemPropertyURLs = ClasspathHelper.forJavaClassPath();
        final Collection<URL> classLoaderURLs = ClasspathHelper.forClassLoader();

        Set<URL> classpathURLs = new HashSet<URL>();

        copyValidClasspathEntries(systemPropertyURLs, classpathURLs);
        copyValidClasspathEntries(classLoaderURLs, classpathURLs);

        logger.debug("Classpath URLs to be scanned: " + classpathURLs);

        Reflections reflections = new Reflections(classpathURLs, new SubTypesScanner());

        applications = reflections.getSubTypesOf(Application.class);

        if(logger.isDebugEnabled()) {
            for (Object appClass : applications.toArray()) {
                logger.debug("JAX-RS Application class found: {}", ((Class<Application>) appClass).getName());
            }
        }
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

        for(String resourceBean : resourceBeans) {
            allResources.add(beanFactory.getType(resourceBean));
        }
        for(String providerBean : providerBeans) {
            providers.add(beanFactory.getType(providerBean));
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

        findJaxrsApplicationClasses();
        findJaxrsResourcesAndProviderClasses(beanFactory);

        if (applications == null || applications.size() == 0) {
            logger.warn("No JAX-RS classes have been found");
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
