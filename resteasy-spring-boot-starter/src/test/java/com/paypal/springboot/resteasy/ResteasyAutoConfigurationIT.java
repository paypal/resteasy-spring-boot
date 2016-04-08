package com.paypal.springboot.resteasy;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.RestAssured;
import com.paypal.springboot.resteasy.sample.TestJaxRsApplication;
import com.paypal.springboot.resteasy.sample.TestSpringBootApplication;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.is;

public class ResteasyAutoConfigurationIT {

    private static ConfigurableApplicationContext context;

    @BeforeSuite
    public static void startApplication() {
        int port = SocketUtils.findAvailableTcpPort();

        String contextPath = "/app";
        String jaxRsApplicationPath = TestJaxRsApplication.APPLICATION_PATH;
        RestAssured.baseURI = "http://localhost:" + port + contextPath + jaxRsApplicationPath;

        context = SpringApplication.run(TestSpringBootApplication.class,
            "--server.port=" + port,
            "--server.context-path=" + contextPath,
            "--spring.resteasy.init.resteasy.role.based.security=true"
        );
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
