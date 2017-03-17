package com.gaocy.sample.util;

import com.gaocy.sample.spider.SpiderBase;

/**
 * Created by godwin on 2017-03-17.
 */
public class SenderUtil {

    public enum MessageLevel {
        ERROR,
        WARN,
        INFO
    }

    public static void sendMessage(MessageLevel level, String message) {
        // TODO
        SpiderBase.logToFile("error.txt", "[" + level + "] " + message);
        System.err.println("[" + level + "] " + message);
    }

}