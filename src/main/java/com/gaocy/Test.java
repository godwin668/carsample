package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.impl.GuaziSpider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Date;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {
    public static void main(String[] args) {
        String[] cities = {"北京", "上海"};
        System.out.println(JSON.toJSONString(cities));
    }
}