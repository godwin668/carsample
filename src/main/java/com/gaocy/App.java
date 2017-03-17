package com.gaocy;

import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class App {

    public static void main(String[] args) {
        // 1 瓜子             https://www.guazi.com/bj/
        // 2 优信             http://www.xin.com/beijing/s
        // 3 人人车            https://www.renrenche.com/bj/ershouche
        // 4 二手车之家          http://www.che168.com/beijing/list/
        // 5 易车                http://beijing.taoche.com/all/
        // 6 澳康达                http://www.akd.cn/carlist
        // 7 车王                 http://www.carking001.com/ershouche
        // 8 车猫
        // 9 好车无忧
        // 10 华夏二手车

        String[] srcArr = ConfUtil.getString("init.src.list").split(",");
        String[] cityArr = ConfUtil.getString("init.city.list").split(",");
        List<Spider> spiderList = new ArrayList<Spider>();
        for (String src : srcArr) {
            Spider spider = SpiderFactory.getSpider(SpiderEnum.GUAZI, cityArr);
            spiderList.add(spider);
        }

        for (Spider spider : spiderList) {
            System.out.println(spider.toString());

            spider.listByCity("bj");
        }
    }
}