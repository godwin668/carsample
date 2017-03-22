package com.gaocy.sample.service.impl;

import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.spider.SpiderRunner;
import com.gaocy.sample.util.ConfUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SpiderServiceImpl {

    @Scheduled(cron = "${init.spider.cron}")
    public void runSpider() {
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