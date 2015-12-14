package com.test;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST endpoint class
 *
 * Created by facarvalho on 12/7/15.
 */
@Path("/echo")
@Produces({ MediaType.APPLICATION_JSON })
@Component
public class Echo {

    @Path("/echo")
    @GET
    public Word echo() {
        Word word = new Word();
        word.setWordId(1);
        word.setWordString("Lua");
        return word;
    }

}
