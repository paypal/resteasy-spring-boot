package com.paypal.springboot.resteasy;

import com.paypal.springboot.resteasy.sample.TestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by facarvalho on 11/25/15.
 */
@ContextConfiguration("classpath:test-config.xml")
public class ResteasyEmbeddedServletInitializerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void postProcessBeanFactory() {
        ServletRegistrationBean servletRegistrationBean = applicationContext.getBean(ServletRegistrationBean.class);
        Assert.assertNotNull(servletRegistrationBean);

        Assert.assertEquals(TestApplication.class.getName(), servletRegistrationBean.getServletName());
        Assert.assertTrue(servletRegistrationBean.isAsyncSupported());
        Assert.assertEquals(TestApplication.class.getName(), servletRegistrationBean.getInitParameters().get("javax.ws.rs.Application"));
        Assert.assertTrue(servletRegistrationBean.isAsyncSupported());
    }

}
