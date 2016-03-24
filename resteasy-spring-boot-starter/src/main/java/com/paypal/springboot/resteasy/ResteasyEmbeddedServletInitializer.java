package com.paypal.springboot.resteasy;

import java.util.Set;

import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.plugins.servlet.ResteasyServletInitializer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * This is a Spring version of {@link ResteasyServletInitializer}
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component
public class ResteasyEmbeddedServletInitializer implements BeanFactoryPostProcessor {

	private Set<Class<? extends Application>> applications;
	private Set<Class<?>> resources;
	private Set<Class<?>> providers;

	public ResteasyEmbeddedServletInitializer() throws ServletException {
		findJaxrsClasses();
	}

	/**
	 * Scan the Classpath searching for classes of type {@link Application}, or
	 * classes annotated with {@link Path}, {@link Provider}
	 * 
	 * @throws ServletException
	 */
	private void findJaxrsClasses() throws ServletException {

		Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath(), new SubTypesScanner(), new TypeAnnotationsScanner(),
				new FilterBuilder().excludePackage(AsynchronousDispatcher.class));

		applications = reflections.getSubTypesOf(Application.class);
		resources = reflections.getTypesAnnotatedWith(Path.class);
		providers = reflections.getTypesAnnotatedWith(Provider.class);
	}

	/**
	 * Check if any JAX-RS related class was found
	 * 
	 * @return true only if there is at least one JAX-RS class
	 */
	private boolean noClasses() {
		if (applications != null && applications.size() != 0) {
			return false;
		}
		if (resources != null && resources.size() != 0) {
			return false;
		}
		if (providers != null && providers.size() != 0) {
			return false;
		}

		return true;
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (noClasses()) {
			return;
		}

		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

		for (Class<? extends Application> applicationClass : applications) {
			ApplicationPath path = applicationClass.getAnnotation(ApplicationPath.class);
			if (path == null) {
				return;
			}

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
