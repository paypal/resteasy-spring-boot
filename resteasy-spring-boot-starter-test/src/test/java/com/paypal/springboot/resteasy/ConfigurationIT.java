package com.paypal.springboot.resteasy;

import com.sample.app.Application;
import io.restassured.response.Response;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * This is an integration test based on a simple sample application (see sample-app project).
 * This class test possible configurations to register JAX-RS application classes.
 *
 * @author facarvalho
 */
public class ConfigurationIT {

    private int configureAndStartApp(Properties properties) {
        return configureAndStartApp(properties, true);
    }

    private int configureAndStartApp(Properties properties, boolean assertPerfectLog) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        if (assertPerfectLog) {
            springApplication.addListeners(new LogbackTestApplicationListener());
        }
        if (properties != null) {
            springApplication.setDefaultProperties(properties);
        }

        int port = SocketUtils.findAvailableTcpPort();
        springApplication.run("--server.port=" + port).registerShutdownHook();

        return port;
    }

    private void appShutdown(int port) {
        Response response = given().basePath("/").port(port).post("/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

    private void assertResourceFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    private void assertResourceNotFound(int port, String basePath) {
        Response response = given().basePath(basePath).port(port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).body("status", equalTo(404)).body("error", equalTo("Not Found"));
    }

    @Test
    public void implicitAutoTest() {
        int port = configureAndStartApp(null);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

    @Test
    public void explicitAutoTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "auto");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

    @Test
    public void beansTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "beans");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

    @Test
    public void propertySpringBeanClassTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.sample.app.JaxrsApplication");

        int port = configureAndStartApp(properties);

        assertResourceFound(port, "sample-app");
        assertResourceNotFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

    @Test
    public void propertyNonSpringBeanClassTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.test.NonSpringBeanJaxrsApplication");

        int port = configureAndStartApp(properties);

        assertResourceNotFound(port, "sample-app");
        assertResourceFound(port, "sample-app-test");
        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

    @Test
    public void invalidClassTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "property");
        properties.put("resteasy.jaxrs.app.classes", "com.foor.bar.NonExistentApplicationClass");

        try {
            configureAndStartApp(properties, false);

            Assert.fail("Expected exception, due to class not found, has not been thrown");
        } catch (BeansException ex) {
            Assert.assertEquals(ex.getCause().getClass(), ClassNotFoundException.class);
            Assert.assertEquals(ex.getCause().getMessage(), "com.foor.bar.NonExistentApplicationClass");
        }
    }

    @Test
    public void scanningTest() {
        Properties properties = new Properties();
        properties.put("resteasy.jaxrs.app.registration", "scanning");

        int port = configureAndStartApp(properties);

        // The real application, everything should work normally
        assertResourceFound(port, "sample-app");

        // This resource should NOT be found because this JAX-RS Application
        // subclass is in a package that is NOT in the list of packages
        // to be scanned by Spring framework
        assertResourceNotFound(port, "sample-app-test");

        // This resource should be found because this JAX-RS Application
        // subclass is actually in a package that is in the list of packages
        // to be scanned by Spring framework, since it is pig-backing in
        // the com.sample.app Application (its actual package is com.sample.app.test)
        assertResourceFound(port, "sample-app-test-two");

        assertResourceNotFound(port, "/");

        appShutdown(port);
    }

}
