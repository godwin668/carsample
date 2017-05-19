package com.gaocy.sample.service.impl;

import com.alibaba.fastjson.JSON;
import com.gaocy.DetailApp;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderRunner;
import com.gaocy.sample.tool.DayCountTool;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.ConfUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SummaryServiceImpl {

    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");

    @Scheduled(cron = "${init.spider.summary.cron}")
    public void runDetailSpider() {
        String yestodayStr = dfDate.format(DateUtils.addDays(new Date(), -1));
        try {
            String[] srcArr = ConfUtil.getString("init.src.list").split(",");
            for (String src : srcArr) { // 来源
                SpiderEnum spider = SpiderEnum.valueOf(src);
                SpiderBase.logToFile("diff", "");
                SpiderBase.logToFile("diff", "-----------------------------");
                SpiderBase.logToFile("diff", "-- " + spider.name());
                SpiderBase.logToFile("diff", "-----------------------------");
                Collection[] oldCommonNewColl = DayCountTool.getOldCommonNewColl(spider, null, yestodayStr);
                SpiderBase.logToFile(yestodayStr + "/stat/" + src + "_ids_old", JSON.toJSONString(oldCommonNewColl[0]));
                SpiderBase.logToFile(yestodayStr + "/stat/" + src + "_ids_remain", JSON.toJSONString(oldCommonNewColl[1]));
                SpiderBase.logToFile(yestodayStr + "/stat/" + src + "_ids_new", JSON.toJSONString(oldCommonNewColl[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SummaryServiceImpl().runDetailSpider();
    }
}