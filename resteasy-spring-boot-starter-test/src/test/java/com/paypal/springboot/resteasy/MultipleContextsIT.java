package com.paypal.springboot.resteasy;

import com.sample.app.Application;
import com.test.multicontexttest.MultiContextTestApp;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

/**
 * This test assures that the RESTEasy and Spring integration, promoted by the starter,
 * does not cause any conflicts when there is more than one application context
 * registered, as reported in the past by this bug (already fixed):
 * https://github.com/paypal/resteasy-spring-boot/issues/51
 *
 * @author facarvalho
 */
public class MultipleContextsIT {

    private int app1Port, app2Port;

    @BeforeClass
    public void setup() {
        app1Port = SocketUtils.findAvailableTcpPort();
        app2Port = SocketUtils.findAvailableTcpPort();

        RestAssured.basePath = "sample-app";
    }

    @Test
    public void test() {
        Properties properties = new Properties();
        properties.put("spring.jmx.enabled", false);

        SpringApplication app1 = new SpringApplication(Application.class);
        app1.setDefaultProperties(properties);
        app1.addListeners(new LogbackTestApplicationListener());
        app1.run("--server.port=" + app1Port).registerShutdownHook();

        SpringApplication app2 = new SpringApplication(MultiContextTestApp.class);
        app2.setDefaultProperties(properties);
        app2.addListeners(new LogbackTestApplicationListener());
        app2.run("--server.port=" + app2Port).registerShutdownHook();

        Response response;

        response = given().port(app1Port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));

        response = given().port(app2Port).body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("I don't want to echo anything today"));
    }

    @AfterClass
    public void shuttingDownApplication() {
        Response response;

        response = given().port(app1Port).basePath("/").post("/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));

        response = given().port(app2Port).basePath("/").post("/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

}
