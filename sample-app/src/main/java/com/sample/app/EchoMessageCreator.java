package com.sample.app;

import org.springframework.stereotype.Component;

/**
 * This bean creates {@link EchoMessage} objects based on
 * echo texts received as input
 *
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
@Component
public class EchoMessageCreator {

    public EchoMessage createEchoMessage(String echoText) {
        return new EchoMessage(echoText);
    }

}
