package com.paypal.springboot.resteasy;

import com.paypal.springboot.resteasy.sample.TestApplication1;
import com.paypal.springboot.resteasy.sample.TestResource1;
import com.paypal.springboot.resteasy.sample.TestResource2;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by facarvalho on 11/25/15.
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@ContextConfiguration("classpath:test-config.xml")
public class ResteasyEmbeddedServletInitializerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void servletsRegistrationTest() {
        Map<String, ServletRegistrationBean> servletRegistrationBeans = applicationContext.getBeansOfType(ServletRegistrationBean.class);
        assertNotNull(servletRegistrationBeans);

        // Although there are 5 sample JAX-RS Application classes, one of them is not annotated with the ApplicationPath annotation!
        assertEquals(servletRegistrationBeans.size(), 4);

        for(String applicationClassName : servletRegistrationBeans.keySet()) {
            testApplicaton(applicationClassName, servletRegistrationBeans.get(applicationClassName));
        }
    }

    private void testApplicaton(String applicationClassName, ServletRegistrationBean servletRegistrationBean) {
        assertEquals(applicationClassName, servletRegistrationBean.getServletName());
        assertTrue(servletRegistrationBean.isAsyncSupported());
        assertEquals(applicationClassName, servletRegistrationBean.getInitParameters().get("javax.ws.rs.Application"));
        assertTrue(servletRegistrationBean.isAsyncSupported());
    }

    @Test
    public void manualResourcesRegistrationTest() {

        final String appClassName = TestApplication1.class.getName();

        ConfigurableListableBeanFactory beanFactoryMock = Mockito.mock(
                ConfigurableListableBeanFactory.class,
                withSettings().extraInterfaces(BeanDefinitionRegistry.class));

        ConfigurableEnvironment configurableEnvironmentMock = Mockito.mock(ConfigurableEnvironment.class);

        when(configurableEnvironmentMock.getProperty("resteasy.jaxrs.app.registration")).thenReturn("property");
        when(configurableEnvironmentMock.getProperty("resteasy.jaxrs.app.classes")).thenReturn(appClassName);
        when(beanFactoryMock.getBean(ConfigurableEnvironment.class)).thenReturn(configurableEnvironmentMock);

        ResteasyEmbeddedServletInitializer servletInitializer = new ResteasyEmbeddedServletInitializer();
        servletInitializer.postProcessBeanFactory(beanFactoryMock);

        ArgumentCaptor<GenericBeanDefinition> beanDefinitionCaptor = ArgumentCaptor.forClass(GenericBeanDefinition.class);
        verify((BeanDefinitionRegistry) beanFactoryMock, times(1)).registerBeanDefinition(eq(appClassName), beanDefinitionCaptor.capture());

        GenericBeanDefinition beanDefinition = beanDefinitionCaptor.getValue();
        assertNotNull(beanDefinition);
        assertFalse(beanDefinition.isAutowireCandidate());
        assertEquals(beanDefinition.getScope(), "singleton");

        Set<Class<?>> providers = new HashSet();
        Set<Class<?>> resources = new HashSet();
        resources.add(TestResource1.class);
        resources.add(TestResource2.class);

        ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        assertNotNull(constructorArgumentValues);
        assertEquals(constructorArgumentValues.getArgumentCount(), 4);
        assertEquals(constructorArgumentValues.getArgumentValue(0, String.class).getValue(), appClassName);
        assertEquals(constructorArgumentValues.getArgumentValue(1, String.class).getValue(), "/myapp1");
        assertEquals(constructorArgumentValues.getArgumentValue(2, Set.class).getValue(), resources);
        assertEquals(constructorArgumentValues.getArgumentValue(3, Set.class).getValue(), providers);
    }

}
