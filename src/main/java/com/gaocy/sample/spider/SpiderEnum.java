package com.gaocy.sample.spider;

/**
 * Created by godwin on 2017-03-17.
 */
public enum SpiderEnum {

    guazi("https://www.guazi.com/bj/buy/o1/"),
    youxin("http://www.xin.com/beijing/s/i1/"),
    che168("http://www.che168.com/beijing/a0_0msdgscncgpi1ltocsp5exx0/"),
    renrenche("https://www.renrenche.com/bj/ershouche/p1");

    private String url;

    SpiderEnum(String url) {
        this.url = url;
    }

    public static void main(String[] args) {
        String s = "guazi";
        SpiderEnum spider = SpiderEnum.valueOf(s);
        System.out.println(spider);
        System.out.println(spider == SpiderEnum.guazi);
    }
}