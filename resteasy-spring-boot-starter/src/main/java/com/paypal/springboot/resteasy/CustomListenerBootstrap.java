package com.paypal.springboot.resteasy;

import javax.servlet.ServletContext;

import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * This class will be removed as soon as RESTEasy 3.0.17.Final is released.
 * See:
 * https://github.com/resteasy/Resteasy/pull/744
 * https://issues.jboss.org/browse/RESTEASY-1305
 *
 * ------------------------------------------------ 
 *
 * Ideally, instead the need of this custom class, the original class
 * {@link ListenerBootstrap} should be modified to allow, not just stand-alone,
 * but also embedded Servlet container scenarios.<br>
 * <br>
 * This custom class is a temporary solution until we fork the Resteasy project
 * where {@link ListenerBootstrap} is, modify such class, and sends a pull
 * request with the necessary change.<br>
 * <br>
 * This class extends the original {@link ListenerBootstrap} overwriting only
 * the method it cares about, which is the one that need changes.<br>
 * <br>
 * As soon as the pull request is sent, and Resteasy incorporates it, then this
 * class can be removed
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class CustomListenerBootstrap extends ListenerBootstrap {
	
	public CustomListenerBootstrap(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	public CustomDispatcherResteasyDeployment createDeployment() {
		ResteasyDeployment regularDeployment = super.createDeployment();
		CustomDispatcherResteasyDeployment customDeployment = new CustomDispatcherResteasyDeployment(regularDeployment);
		
		return customDeployment;
	}

}
