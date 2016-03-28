package com.paypal.springboot.resteasy;

import java.util.Set;

import javax.servlet.Servlet;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component(ResteasyApplicationBuilder.BEAN_NAME)
public class ResteasyApplicationBuilder {

	public static final String BEAN_NAME = "JaxrsApplicationServletBuilder";

	private static final Logger logger = LoggerFactory.getLogger(ResteasyApplicationBuilder.class);

	public ServletRegistrationBean build(String applicationClassName, String path, Set<Class<?>> resources, Set<Class<?>> providers) {
		Servlet servlet = new HttpServlet30Dispatcher();

		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet);

		servletRegistrationBean.setName(applicationClassName);
		servletRegistrationBean.setLoadOnStartup(1);
		servletRegistrationBean.setAsyncSupported(true);
		servletRegistrationBean.addInitParameter("javax.ws.rs.Application", applicationClassName);

		if (path != null) {
			String mapping = path;
			if (!mapping.startsWith("/"))
				mapping = "/" + mapping;
			String prefix = mapping;
			if (!"/".equals(prefix) && "/".endsWith(prefix))
				prefix = prefix.substring(0, prefix.length() - 1);
			if (mapping.endsWith("/"))
				mapping += "*";
			else
				mapping += "/*";
			// resteasy.servlet.mapping.prefix
			servletRegistrationBean.addInitParameter("resteasy.servlet.mapping.prefix", prefix);
			servletRegistrationBean.addUrlMappings(mapping);
		}

		if (resources.size() > 0) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (Class<?> resource : resources) {
				if (first) {
					first = false;
				} else {
					builder.append(",");
				}

				builder.append(resource.getName());
			}
			servletRegistrationBean.addInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_RESOURCES, builder.toString());
		}
		if (providers.size() > 0) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (Class<?> provider : providers) {
				if (first) {
					first = false;
				} else {
					builder.append(",");
				}
				builder.append(provider.getName());
			}
			servletRegistrationBean.addInitParameter(ResteasyContextParameters.RESTEASY_SCANNED_PROVIDERS, builder.toString());
		}

		logger.debug("ServletRegistrationBean has just bean created for JAX-RS class" + applicationClassName);

		return servletRegistrationBean;
	}

}
