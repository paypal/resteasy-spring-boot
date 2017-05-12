package com.test.multicontexttest;

import org.springframework.stereotype.Component;

/**
 * This bean creates {@link EchoMessage} objects but,
 * different than the original application it was copied from,
 * ignoring echo texts received as input
 *
 * @author facarvalho
 */
@Component
public class EchoMessageCreator {

    public EchoMessage createEchoMessage(String echoText) {
        return new EchoMessage("I don't want to echo anything today");
    }

}
