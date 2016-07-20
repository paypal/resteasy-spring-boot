package com.paypal.springboot.resteasy;

import com.paypal.springboot.resteasy.sample.TestApplication1;
import com.paypal.springboot.resteasy.sample.TestApplication4;
import com.paypal.springboot.resteasy.sample.TestResource1;
import static org.mockito.Mockito.*;

import com.paypal.springboot.resteasy.sample.TestResource2;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testng.annotations.Test;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by facarvalho on 7/19/16.
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class JaxrsAppRegistrationTest {

    private static final String DEFINITION_PROPERTY = "resteasy.jaxrs.app.registration";
    private static final String APP_CLASSES_PROPERTY = "resteasy.jaxrs.app";

    @Test
    public void nullTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn(null);

        test(configurableEnvironmentMock, 2, 1, 1, 1, 0, 1, 1);
    }

    @Test
    public void autoTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("auto");

        test(configurableEnvironmentMock, 2, 1, 1, 1, 0, 1, 1);
    }

    @Test
    public void beansTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("beans");

        test(configurableEnvironmentMock, 1, 1, 1, 0, 0, 1, 0);
    }

    @Test
    public void propertyTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("property");
        when(configurableEnvironmentMock.getProperty(APP_CLASSES_PROPERTY)).thenReturn("com.paypal.springboot.resteasy.sample.TestApplication3, com.paypal.springboot.resteasy.sample.TestApplication4,com.paypal.springboot.resteasy.sample.TestApplication2");

        test(configurableEnvironmentMock, 2, 0, 0, 1, 0, 1, 0);
    }

    @Test
    public void scanningTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("scanning");

        test(configurableEnvironmentMock, 1, 0, 1, 1, 0, 1, 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Property " + DEFINITION_PROPERTY +
                    " has not been properly set, value blah is invalid. JAX-RS Application classes registration is being set to AUTO.")
    public void invalidRegistrationTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("blah");

        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(beanFactory.getBean(ConfigurableEnvironment.class)).thenReturn(configurableEnvironmentMock);

        ResteasyEmbeddedServletInitializer resteasyEmbeddedServletInitializer = new ResteasyEmbeddedServletInitializer();
        resteasyEmbeddedServletInitializer.postProcessBeanFactory(beanFactory);
    }

    @Test(expectedExceptions = BeansException.class)
    public void classNotFoundTest() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("property");
        when(configurableEnvironmentMock.getProperty(APP_CLASSES_PROPERTY)).thenReturn("com.paypal.springboot.resteasy.sample.TestApplication3, com.paypal.springboot.resteasy.sample.TestApplication4,com.paypal.springboot.resteasy.sample.TestApplication9");

        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        when(beanFactory.getBean(ConfigurableEnvironment.class)).thenReturn(configurableEnvironmentMock);

        ResteasyEmbeddedServletInitializer resteasyEmbeddedServletInitializer = new ResteasyEmbeddedServletInitializer();
        resteasyEmbeddedServletInitializer.postProcessBeanFactory(beanFactory);
    }

    @Test
    public void testPropertyNoApps() {
        ConfigurableEnvironment configurableEnvironmentMock = mock(ConfigurableEnvironment.class);
        when(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY)).thenReturn("property");

        test(configurableEnvironmentMock, 2, 0, 0, 0, 0, 0, 0);
    }

    private void test( ConfigurableEnvironment configurableEnvironmentMock,
                       int getBeanT, int getBeansOfTypeT,
                       int testApp1T, int testApp2T, int testApp3T, int testApp4T, int testApp5T) {

        ConfigurableListableBeanFactory beanFactory = mock(
                ConfigurableListableBeanFactory.class,
                withSettings().extraInterfaces(BeanDefinitionRegistry.class)
        );

        when(beanFactory.getBean(ConfigurableEnvironment.class)).thenReturn(configurableEnvironmentMock);
        when(beanFactory.getBeanNamesForAnnotation(Path.class)).thenReturn(new String[]{"testResource1", "testResource2"});
        when(beanFactory.getType("testResource1")).thenReturn((Class) TestResource1.class);
        when(beanFactory.getType("testResource2")).thenReturn((Class) TestResource2.class);

        if(configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY) != null && configurableEnvironmentMock.getProperty(DEFINITION_PROPERTY).equals("beans")) {
            // Although TestApplication1 and TestApplication4 are not really Spring beans, here we are simulating
            // they are to see how the JAX-RS Application registration behaves
            Map<String,Application> applicationsMap = new HashMap<String, Application>();
            applicationsMap.put("testApplication1", new TestApplication1());
            applicationsMap.put("testApplication4", new TestApplication4());
            when(beanFactory.getBeansOfType(Application.class, true, false)).thenReturn(applicationsMap);
        }

        ResteasyEmbeddedServletInitializer resteasyEmbeddedServletInitializer = new ResteasyEmbeddedServletInitializer();
        resteasyEmbeddedServletInitializer.postProcessBeanFactory(beanFactory);

        verify(beanFactory, VerificationModeFactory.times(getBeanT)).getBean(ConfigurableEnvironment.class);
        verify(beanFactory, VerificationModeFactory.times(getBeansOfTypeT)).getBeansOfType(Application.class, true, false);
        verify(beanFactory, VerificationModeFactory.times(1)).getBeanNamesForAnnotation(Path.class);
        verify(beanFactory, VerificationModeFactory.times(1)).getBeanNamesForAnnotation(Provider.class);
        verify(beanFactory, VerificationModeFactory.times(2)).getType(anyString());

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        verify(registry, VerificationModeFactory.times(testApp1T)).registerBeanDefinition(eq("com.paypal.springboot.resteasy.sample.TestApplication1"), any(GenericBeanDefinition.class));
        verify(registry, VerificationModeFactory.times(testApp2T)).registerBeanDefinition(eq("com.paypal.springboot.resteasy.sample.TestApplication2"), any(GenericBeanDefinition.class));
        verify(registry, VerificationModeFactory.times(testApp3T)).registerBeanDefinition(eq("com.paypal.springboot.resteasy.sample.TestApplication3"), any(GenericBeanDefinition.class));
        verify(registry, VerificationModeFactory.times(testApp4T)).registerBeanDefinition(eq("com.paypal.springboot.resteasy.sample.TestApplication4"), any(GenericBeanDefinition.class));
        verify(registry, VerificationModeFactory.times(testApp5T)).registerBeanDefinition(eq("com.paypal.springboot.resteasy.sample.TestApplication5"), any(GenericBeanDefinition.class));
    }

}
