package com.gaocy;

import com.gaocy.sample.service.impl.SpiderServiceImpl;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.spider.SpiderRunner;
import com.gaocy.sample.util.ConfUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class App {

    public static void main(String[] args) {
        SpiderServiceImpl service = new SpiderServiceImpl();
        service.runSpider();
    }
}