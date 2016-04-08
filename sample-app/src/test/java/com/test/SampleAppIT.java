package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.is;

public class SampleAppIT {

    private static ConfigurableApplicationContext context;

    @BeforeSuite
    public static void startApplication() {
        int port = SocketUtils.findAvailableTcpPort();

        RestAssured.baseURI = "http://localhost:" + port + "/sample-app";

        context = SpringApplication.run(SampleAppApplication.class, "--server.port=" + port);
    }

    @AfterSuite
    public static void stopApplication() {
        context.close();
    }

    @Test
    public void getEchoReturns200() {
        expect()
            .statusCode(200)
            .body("wordId", is(1))
            .body("wordString", is("Lua"))

            .given()
            .get("/echo/echo");
    }

    @Test
    public void getDenyReturns403() {
        expect()
            .statusCode(403)

            .given()
            .get("/echo/deny");
    }

}
