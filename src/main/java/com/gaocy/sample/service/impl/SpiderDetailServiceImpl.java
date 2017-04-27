package com.gaocy.sample.service.impl;

import com.gaocy.DetailApp;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.spider.SpiderRunner;
import com.gaocy.sample.util.ConfUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SpiderDetailServiceImpl {

    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");

    @Scheduled(cron = "${init.spider.detail.cron}")
    public void runDetailSpider() {
        String dateStr = dfDate.format(DateUtils.addDays(new Date(), -1));
        SpiderEnum[] spiderEnumArr = { SpiderEnum.youxin, SpiderEnum.che168 };
        List<String> cityList = null;
        for (SpiderEnum spider : spiderEnumArr) {
            DetailApp app = new DetailApp(dateStr, spider, cityList);
            SpiderRunner.es.submit(app);
        }
    }

}