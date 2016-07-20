package com.paypal.springboot.resteasy;

import org.apache.commons.io.FilenameUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to scan the classpath searching for
 * JAX-RS Application sub-classes
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public abstract class JaxrsApplicationScanner {

    private static final Logger logger = LoggerFactory.getLogger(JaxrsApplicationScanner.class);

    private static Set<Class<? extends Application>> applications;

    public static Set<Class<? extends Application>> getApplications() {
        if(applications == null) {
            applications = findJaxrsApplicationClasses();
        }

        return applications;
    }

    /*
     * Scan the classpath looking for JAX-RS Application sub-classes
     */
    private static Set<Class<? extends Application>> findJaxrsApplicationClasses() {
        logger.info("Scanning classpath to find JAX-RS Application classes");

        final Collection<URL> systemPropertyURLs = ClasspathHelper.forJavaClassPath();
        final Collection<URL> classLoaderURLs = ClasspathHelper.forClassLoader();

        Set<URL> classpathURLs = new HashSet<URL>();

        copyValidClasspathEntries(systemPropertyURLs, classpathURLs);
        copyValidClasspathEntries(classLoaderURLs, classpathURLs);

        logger.debug("Classpath URLs to be scanned: " + classpathURLs);

        Reflections reflections = new Reflections(classpathURLs, new SubTypesScanner());

        return reflections.getSubTypesOf(Application.class);
    }

    /*
     * Copy all entries that are a JAR file or a directory
     */
    private static void copyValidClasspathEntries(Collection<URL> source, Set<URL> destination) {
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

}
