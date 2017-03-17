package com.gaocy;

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
        String[] srcArr = ConfUtil.getString("init.src.list").split(",");
        String[] cityArr = ConfUtil.getString("init.city.list").split(",");
        List<Spider> spiderList = new ArrayList<Spider>();
        for (String src : srcArr) {
            Spider spider = SpiderFactory.getSpider(SpiderEnum.valueOf(src), cityArr);
            SpiderRunner runner = new SpiderRunner(spider);
            runner.run();
        }
    }
}