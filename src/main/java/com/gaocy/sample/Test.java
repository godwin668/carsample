package com.gaocy.sample;

/**
 * Created by godwin on 2017-03-17.
 */
public class Test {

    public static void main(String[] args) {
        String s = "https://www.guazi.com/#{city}/buy/o#{pageIndex}";
        System.out.println(s.replaceFirst("#\\{city\\}", "bj"));

    }

}