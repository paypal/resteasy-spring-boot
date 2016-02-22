/*
This class is based off https://github.com/resteasy/Resteasy/blob/3.0.16.Final/jaxrs/resteasy-jaxrs/src/main/java/org/jboss/resteasy/spi/ResteasyDeployment.java.
A few modifications were made on it.
*/
package com.paypal.springboot.resteasy;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.AcceptHeaderByFileSuffixFilter;
import org.jboss.resteasy.core.AcceptParameterHttpPreprocessor;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.ServerFormUrlEncodedProvider;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.InternalDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Ideally, instead the need of this custom class, the original class
 * {@link ResteasyDeployment} should be modified to allow, not just stand-alone,
 * but also embedded Servlet container scenarios.<br>
 * <br>
 * This custom class is a temporary solution until we fork the Resteasy project
 * where {@link ResteasyDeployment} is, modify such class, and sends a pull
 * request with the necessary change.<br>
 * <br>
 * This class extends the original {@link ResteasyDeployment} overwriting only
 * the method it cares about, which is the one that need changes. Search for
 * CUSTOM CODE BEGINS and CUSTOM CODE ENDS comments. That is the only change
 * section, everything else in that method is the same as the original. <br>
 * <br>
 * As soon as the pull request is sent, and Resteasy incorporates it, then this
 * class can be removed
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class CustomDispatcherResteasyDeployment extends ResteasyDeployment
{

	private final static Logger logger = Logger.getLogger(ResteasyDeployment.class);

	public CustomDispatcherResteasyDeployment(ResteasyDeployment regularDeployment) {
		this.actualProviderClasses = regularDeployment.getActualProviderClasses();
	}

   public void start()
   {
      // it is very important that each deployment create their own provider factory
      // this allows each WAR to have their own set of providers 
      if (providerFactory == null) providerFactory = new ResteasyProviderFactory();
      providerFactory.setRegisterBuiltins(registerBuiltin);

      if (deploymentSensitiveFactoryEnabled)
      {
         // the ThreadLocalResteasyProviderFactory pushes and pops this deployments parentProviderFactory
         // on a ThreadLocal stack.  This allows each application/WAR to have their own parentProviderFactory
         // and still be able to call ResteasyProviderFactory.getInstance()
         if (!(providerFactory instanceof ThreadLocalResteasyProviderFactory))
         {
            if (ResteasyProviderFactory.peekInstance() == null || !(ResteasyProviderFactory.peekInstance() instanceof ThreadLocalResteasyProviderFactory))
            {

               threadLocalProviderFactory = new ThreadLocalResteasyProviderFactory(providerFactory);
               ResteasyProviderFactory.setInstance(threadLocalProviderFactory);
            }
         }
      }
      else
      {
         ResteasyProviderFactory.setInstance(providerFactory);
      }


      if (asyncJobServiceEnabled)
      {
         AsynchronousDispatcher asyncDispatcher = new AsynchronousDispatcher(providerFactory);
         asyncDispatcher.setMaxCacheSize(asyncJobServiceMaxJobResults);
         asyncDispatcher.setMaxWaitMilliSeconds(asyncJobServiceMaxWait);
         asyncDispatcher.setThreadPoolSize(asyncJobServiceThreadPoolSize);
         asyncDispatcher.setBasePath(asyncJobServiceBasePath);
         asyncDispatcher.getUnwrappedExceptions().addAll(unwrappedExceptions);
         dispatcher = asyncDispatcher;
         asyncDispatcher.start();
      }
      else
      {

    	  /*
    	   * CUSTOM CODE BEGINS
    	   */
    	  
    	  if(dispatcher == null) {
    		  SynchronousDispatcher dis = new SynchronousDispatcher(providerFactory);
    		  dis.getUnwrappedExceptions().addAll(unwrappedExceptions);
    		  dispatcher = dis;
    	  }
    	  
    	  /*
    	   * CUSTOM CODE ENDS
    	   */
      }
      registry = dispatcher.getRegistry();
      if (widerRequestMatching)
      {
         ((ResourceMethodRegistry)registry).setWiderMatching(widerRequestMatching);
      }


      dispatcher.getDefaultContextObjects().putAll(defaultContextObjects);
      dispatcher.getDefaultContextObjects().put(Configurable.class, providerFactory);
      dispatcher.getDefaultContextObjects().put(Providers.class, providerFactory);
      dispatcher.getDefaultContextObjects().put(Registry.class, registry);
      dispatcher.getDefaultContextObjects().put(Dispatcher.class, dispatcher);
      dispatcher.getDefaultContextObjects().put(InternalDispatcher.class, InternalDispatcher.getInstance());

      // push context data so we can inject it
      Map contextDataMap = ResteasyProviderFactory.getContextDataMap();
      contextDataMap.putAll(dispatcher.getDefaultContextObjects());
      
      try
      {
         if (injectorFactoryClass != null)
         {
            InjectorFactory injectorFactory;
            try
            {
               Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(injectorFactoryClass);
               injectorFactory = (InjectorFactory) clazz.newInstance();
            }
            catch (ClassNotFoundException cnfe)
            {
               throw new RuntimeException("Unable to find InjectorFactory implementation.", cnfe);
            }
            catch (Exception e)
            {
               throw new RuntimeException("Unable to instantiate InjectorFactory implementation.", e);
            }

            providerFactory.setInjectorFactory(injectorFactory);
         }

         // feed context data map with constructed objects 
         // see ResteasyContextParameters.RESTEASY_CONTEXT_OBJECTS
         if (constructedDefaultContextObjects != null && constructedDefaultContextObjects.size() > 0)
         {
            for (Map.Entry<String, String> entry : constructedDefaultContextObjects.entrySet())
            {
               Class<?> key = null;
               try
               {
                  key = Thread.currentThread().getContextClassLoader().loadClass(entry.getKey());
               }
               catch (ClassNotFoundException e)
               {
                  throw new RuntimeException("Unable to instantiate context object " + entry.getKey(), e);
               }
               Object obj = createFromInjectorFactory(entry.getValue(), providerFactory);
               logger.debug("Creating context object <" + entry.getKey() + " : " + entry.getValue() + ">");
               defaultContextObjects.put(key, obj);
               dispatcher.getDefaultContextObjects().put(key, obj);
               contextDataMap.put(key, obj);

            }
         }

         // Interceptor preferences should come before provider registration or builtin.

         if (interceptorPrecedences != null)
         {
            for (String precedence : interceptorPrecedences)
            {
               providerFactory.appendInterceptorPrecedence(precedence.trim());
            }
         }

         if (interceptorBeforePrecedences != null)
         {
            for (Map.Entry<String, String> ext : interceptorBeforePrecedences.entrySet())
            {
               providerFactory.insertInterceptorPrecedenceBefore(ext.getKey().trim(), ext.getValue().trim());
            }
         }
         if (interceptorAfterPrecedences != null)
         {
            for (Map.Entry<String, String> ext : interceptorAfterPrecedences.entrySet())
            {
               providerFactory.insertInterceptorPrecedenceAfter(ext.getKey().trim(), ext.getValue().trim());
            }
         }


         if (securityEnabled)
         {
            providerFactory.register(RoleBasedSecurityFeature.class);
         }


         if (registerBuiltin)
         {
            providerFactory.setRegisterBuiltins(true);
            RegisterBuiltin.register(providerFactory);

            // having problems using form parameters from container for a couple of TCK tests.  I couldn't figure out
            // why, specifically:
            // com/sun/ts/tests/jaxrs/spec/provider/standardhaspriority/JAXRSClient.java#readWriteMapProviderTest_from_standalone                                               Failed. Test case throws exception: [JAXRSCommonClient] null failed!  Check output for cause of failure.
            // com/sun/ts/tests/jaxrs/spec/provider/standardwithjaxrsclient/JAXRSClient.java#mapElementProviderTest_from_standalone                                             Failed. Test case throws exception: returned MultivaluedMap is null
            providerFactory.registerProviderInstance(new ServerFormUrlEncodedProvider(useContainerFormParams), null, null, true);
         }
         else
         {
            providerFactory.setRegisterBuiltins(false);
         }

         if (applicationClass != null)
         {
            application = createApplication(applicationClass, dispatcher, providerFactory);

         }

         // register all providers
         registration();

         if (paramMapping != null)
         {
            providerFactory.getContainerRequestFilterRegistry().registerSingleton(new AcceptParameterHttpPreprocessor(paramMapping));
         }

         AcceptHeaderByFileSuffixFilter suffixNegotiationFilter = null;
         if (mediaTypeMappings != null)
         {
            Map<String, MediaType> extMap = new HashMap<String, MediaType>();
            for (Map.Entry<String, String> ext : mediaTypeMappings.entrySet())
            {
               String value = ext.getValue();
               extMap.put(ext.getKey().trim(), MediaType.valueOf(value.trim()));
            }

            if (suffixNegotiationFilter == null)
            {
               suffixNegotiationFilter = new AcceptHeaderByFileSuffixFilter();
               providerFactory.getContainerRequestFilterRegistry().registerSingleton(suffixNegotiationFilter);
            }
            suffixNegotiationFilter.setMediaTypeMappings(extMap);
         }


         if (languageExtensions != null)
         {
            if (suffixNegotiationFilter == null)
            {
               suffixNegotiationFilter = new AcceptHeaderByFileSuffixFilter();
               providerFactory.getContainerRequestFilterRegistry().registerSingleton(suffixNegotiationFilter);
            }
            suffixNegotiationFilter.setLanguageMappings(languageExtensions);
         }
      }
      finally
      {
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
}
