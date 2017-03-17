package com.gaocy.sample;

/**
 * Created by godwin on 2017-03-17.
 */
public class Test {

    public static void main(String[] args) {
        String s = "2013年4月";
        String dateStr = s.replaceFirst("(\\d+)年(\\d+)月", "$1$2");
        if (dateStr.length() == 5) {
            dateStr = dateStr.replaceFirst("(\\d{4})(\\d{1})", "$10$2");
        }
        System.out.println(dateStr);
    }
}