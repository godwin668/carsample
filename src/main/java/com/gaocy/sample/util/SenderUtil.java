package com.gaocy.sample.util;

/**
 * Created by godwin on 2017-03-17.
 */
public class SenderUtil {

    static enum MessageLevel {
        ERROR,
        WARN,
        INFO;
    }

    public static void sendMessage(MessageLevel level, String message) {
        // TODO
        System.err.println("[" + level + "] " + message);
    }

}