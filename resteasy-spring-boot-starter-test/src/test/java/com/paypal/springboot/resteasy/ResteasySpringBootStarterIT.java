package com.paypal.springboot.resteasy;

import com.test.Application;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * This is an integration test based on a simple sample application (see sample-app project)
 *
 * @author facarvalho
 */
public class RestEasySpringBootStarterIT {

    @BeforeClass
    public void beforeClass() {
        RestAssured.basePath = "sample-app";
        int port = SocketUtils.findAvailableTcpPort();
        RestAssured.port = port;
        SpringApplication.run(Application.class, "--server.port=" + port).registerShutdownHook();
    }

    @Test
    public void happyPathTest() {
        Response response = given().body("is there anybody out there?").post("/echo");
        response.then().statusCode(200).body("timestamp", notNullValue()).body("echoText", equalTo("is there anybody out there?"));
    }

    @Test
    public void invalidUriPathTest() {
        // Notice "eco" is supposed to result in 404
        Response response = given().body("is there anybody out there?").post("/eco");
        response.then().statusCode(404).body(equalTo(""));
    }

    @Test
    public void invalidBaseUrlTest() {
        // Notice "sampl-ap" is supposed to result in 404
        Response response = given().basePath("sampl-app").body("is there anybody out there?").post("/echo");
        response.then().statusCode(404).body("status", equalTo(404)).body("error", equalTo("Not Found"));
    }

    @Test
    public void actuatorTest() throws InterruptedException {
        Response response = given().basePath("/").get("/health");
        response.then().statusCode(200).body("status", equalTo("UP"));
    }

}
