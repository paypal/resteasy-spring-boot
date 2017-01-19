package com.test;

/**
 * A simple echo message, containing the text to be echoed
 * and timestamp of the moment the message was created
 *
 * @author facarvalho
 */
public class EchoMessage {

    private long timestamp;
    private String echoText;

    public EchoMessage(String echoText) {
        timestamp = System.currentTimeMillis();
        this.echoText = echoText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getEchoText() {
        return echoText;
    }

}