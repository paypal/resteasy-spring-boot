package com.paypal.springboot.resteasy.test;

import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;

import io.restassured.RestAssured;

/**
 * Integration test for a resteasy service. This test will start the spring boot
 * application in an embedded container and executes real http requests against
 * it. The responses are verified in the test.<br>
 *
 * @author Christian Fehmer
 *
 */
public class RestEasyApplicationTest {

    private static ConfigurableApplicationContext context;
    private int port;
    private Class<?> applicationClass;
    private String basePath;

    /**
     * initializes a test for the given application
     *
     * @param applicationClass
     *            the main application class, most likely annotated with
     *            {@link SpringBootApplication} and containing a static main
     *            method with a <code>SpringApplication.run(...)</code>
     *            statement.
     * @param basePath
     *            base path of the service under test, e.g. /sample-app/echo.
     *            Contains of the {@link javax.ws.rs.ApplicationPath} and
     *            {@link javax.ws.rs.Path} annotations of your application class
     *            and the jaxrs endpoint.
     */
    public RestEasyApplicationTest(Class<?> applicationClass, String basePath) {
        this.applicationClass = applicationClass;
        this.basePath = basePath;

    }

    /**
     * Starts the application in an embedded container once per test class on a
     * random port.
     */
    @Before
    public void startServer() {
        if (context == null) {

            // find a free port
            port = SocketUtils.findAvailableTcpPort(10000);

            // start the application on the specific port. Note that this test
            // class is also part of the applications spring context
            context = SpringApplication.run(new Object[] { applicationClass, this.getClass() }, new String[] { "--server.port=" + port });

            // set base url for all restassured interactions
            RestAssured.baseURI = "http://localhost:" + port + basePath;
        }
    }

    /**
     * shutdown the application after the test class is finished
     */
    @AfterClass
    public static void stopServer() {
        if (context != null)
            context.close();
    }
}
