package com.gaocy.sample.spider;

/**
 * Created by godwin on 2017-03-17.
 */
public enum SpiderEnum {

    GUAZI("https://www.guazi.com/bj/buy/o1/"),
    YOUXIN("http://www.xin.com/beijing/s/i1/");

    private String url;

    SpiderEnum(String url) {
        this.url = url;
    }

    public static void main(String[] args) {
        String s = "GUAZI";
        SpiderEnum spider = SpiderEnum.valueOf(s);
        System.out.println(spider);
        System.out.println(spider == SpiderEnum.GUAZI);
    }
}