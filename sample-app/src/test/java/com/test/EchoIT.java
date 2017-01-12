package com.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.paypal.springboot.resteasy.test.RestEasyApplicationTest;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the {@link Echo} endpoint. This test will start the
 * {@link Application} in an embedded container and executes real http requests
 * against it. The responses are verified in the test.<br>
 *
 * {@link Echo} has a dependency to the {@link WordService}. We don't want to
 * test the {@link WordService}, its already done by the
 * {@link WordServiceTest}. We mock the {@link WordService} and use the mock
 * inside the running {@link Application} on the embedded container.
 *
 * @author Christian Fehmer
 *
 */
public class EchoIT extends RestEasyApplicationTest {

    /**
     * Mock for te {@link WordService}. Has to be static because we want to
     * share the instance between this test and the running {@link Application}
     * in the embedded container.
     */
    private static WordService wordsMock = mock(WordService.class);

    public EchoIT() {
        super(Application.class, "/sample-app/echo");
    }

    /**
     * Provide the mock for {@link WordService} to the spring context of the
     * running {@link Application}. We use {@link Bean} to produce the bean and
     * {@link Primary} to tell spring that we want to use this instance and not
     * the instance of the {@link WordService} already existing in the spring
     * context from the {@link Application}
     */
    @Bean
    @Primary
    public WordService wordsMock() {
        return EchoIT.wordsMock;
    }

    /**
     * We need to reset all mocks interactions and return definitions before
     * each test method.
     */
    @Before
    public void cleanMocks() {
        reset(wordsMock);
    }

    /**
     * After each test method we want to ensure that only the interactions we
     * expect and define with <code>verify(mock)</code> are done.
     */
    @After
    public void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(wordsMock);

    }

    /**
     * Mock the {@link WordService} to reply 2, Node. HTTP GET the /echo
     * resource and verify the response.
     */
    @Test
    public void providesNodeWhenGetOnEcho() {
        // GIVEN

        // mock words.getWord to return 2, Node
        when(wordsMock.getWord()).thenReturn(new Word().setWordId(2L).setWordString("Node"));

        // prepare rest request
        RequestSpecification request = given();

        // WHEN

        // request /echo from service
        Response response = request.get("/echo");

        // THEN

        // verify response code 200 with the expected word 2, Node
        response.then()
                .statusCode(200)
                .body("wordId", equalTo(2))
                .body("wordString", equalTo("Node"));

        // not needed, this is an other way to make assertions on the response
        assertThat(response.getStatusCode(), equalTo(200));

        // verify words is called
        verify(wordsMock).getWord();
    }

    /**
     * Mock the {@link WordService}, this time we reply 4, Piet. HTTP GET the
     * /echo resource and verify the response.
     */
    @Test
    public void providesPietWhenGetOnEcho() {
        // GIVEN

        // mock words.getWord to return 4, Piet
        when(wordsMock.getWord()).thenReturn(new Word().setWordId(4L).setWordString("Piet"));

        // prepare rest request
        RequestSpecification request = given();

        // WHEN

        // request /echo from service
        Response response = request.get("/echo");

        // THEN

        // verify response code 200 with the expected word 4, Piet
        response.then()
                .statusCode(200)
                .body("wordId", equalTo(4))
                .body("wordString", equalTo("Piet"));

        // not needed, this is an other way to make assertions on the response
        assertThat(response.getStatusCode(), equalTo(200));

        // verify words is called
        verify(wordsMock).getWord();
    }

}
