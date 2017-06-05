package com.paypal.springboot.resteasy;

import com.sample.app.Application;
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;

/**
 * Integration tests for RESTEasy Asynchronous Job Service
 *
 * @author facarvalho
 */
public class AsyncJobIT {

    @BeforeClass
    public void setUp() {
        int appPort = SocketUtils.findAvailableTcpPort();

        RestAssured.basePath = "sample-app";
        RestAssured.port = appPort;

        Properties properties = new Properties();
        properties.put("server.context-parameters.resteasy.async.job.service.enabled", true);

        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(properties);
        app.addListeners(new LogbackTestApplicationListener());
        app.run("--server.port=" + appPort).registerShutdownHook();
    }

    @Test
    public void regularRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void asyncRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo?asynch=true");
        response.then().statusCode(202).body(isEmptyString());

        String location = response.getHeader("Location");
        response = given().get(location + "?wait=50");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void fireAndForgetRequestTest() {
        Response response = given().body("is there anybody out there?").post("/echo?oneway=true");
        response.then().statusCode(202).body(isEmptyString());
    }

    @AfterClass
    public void shuttingDownApplication() {
        Response response = given().basePath("/").post("/shutdown");
        response.then().statusCode(200).body("message", equalTo("Shutting down, bye..."));
    }

}
