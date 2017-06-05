package com.paypal.springboot.resteasy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class to scan the classpath under the specified packages
 * searching for JAX-RS Application sub-classes
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public abstract class JaxrsApplicationScanner {

    private static final Logger logger = LoggerFactory.getLogger(JaxrsApplicationScanner.class);

    private static Set<Class<? extends Application>> applications;

    public static Set<Class<? extends Application>> getApplications(List<String> packagesToBeScanned) {
        if(applications == null) {
            applications = findJaxrsApplicationClasses(packagesToBeScanned);
        }

        return applications;
    }

    /*
     * Scan the classpath under the specified packages looking for JAX-RS Application sub-classes
     */
    private static Set<Class<? extends Application>> findJaxrsApplicationClasses(List<String> packagesToBeScanned) {
        logger.info("Scanning classpath to find JAX-RS Application classes");

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Application.class));

        Set<BeanDefinition> candidates = new HashSet<BeanDefinition>();
        Set<BeanDefinition> candidatesSubSet;

        for (String packageToScan : packagesToBeScanned) {
            candidatesSubSet = scanner.findCandidateComponents(packageToScan);
            candidates.addAll(candidatesSubSet);
        }

        Set<Class<? extends Application>> classes = new HashSet<Class<? extends Application>>();
        ClassLoader classLoader = JaxrsApplicationScanner.class.getClassLoader();
        Class<? extends Application> type;
        for (BeanDefinition candidate : candidates) {
            try {
                type = (Class<? extends Application>) ClassUtils.forName(candidate.getBeanClassName(), classLoader);
                classes.add(type);
            } catch (ClassNotFoundException e) {
                logger.error("JAX-RS Application subclass could not be loaded", e);
            }
        }

        // We don't want the JAX-RS Application class itself in there
        classes.remove(Application.class);

        return classes;
    }

}
