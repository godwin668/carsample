package com.gaocy.sample.tool;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-29.
 */
public class DayCountTool {

    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static File baseDir = new File(ConfUtil.getString("init.log.base.url"));

    public static void main(String[] args) {
        int startDate = 20170322;
        int endDate = 20170329;

        String[] srcArr = ConfUtil.getString("init.src.list").split(",");
        String[] cityArr = ConfUtil.getString("init.city.list").split(",");
        List<Spider> spiderList = new ArrayList<Spider>();
        for (String src : srcArr) { // 来源
            SpiderEnum spider = SpiderEnum.valueOf(src);
            SpiderBase.logToFile("diff", "-----------------------------");
            SpiderBase.logToFile("diff", spider.name());
            for (String city : cityArr) {
                for (int dateInt = startDate; dateInt <= endDate; dateInt++) {  // 日期
                    int[] oldCommonNewCount = getOldCommonNewCount(spider, city, "" + dateInt);
                    System.out.println("" + dateInt + " " + spider.name() + " " + city + " " + JSON.toJSONString(oldCommonNewCount));
                }
            }
        }
    }

    public static int[] getOldCommonNewCount(SpiderEnum spider, String city, String dateStr) {
        int[] diffCount = new int[3];
        try {
            Date yestoday = DateUtils.addDays(dfDate.parse(dateStr), -1);
            String dateYestodayStr = dfDate.format(yestoday);
            File todayFile = new File(baseDir, dateStr + "/" + spider.name().toLowerCase() + ".txt");
            File yestoodayFile = new File(baseDir, dateYestodayStr + "/" + spider.name().toLowerCase() + ".txt");
            if (todayFile.isFile() && yestoodayFile.isFile()) {
                Set<String> yestodayIdSet = getIdSet(spider, city, dateYestodayStr);
                Set<String> todayIdSet = getIdSet(spider, city, dateStr);
                Collection commonIdSet = CollectionUtils.intersection(todayIdSet, yestodayIdSet);
                Collection oldColl = CollectionUtils.subtract(yestodayIdSet, todayIdSet);
                Collection newColl = CollectionUtils.subtract(todayIdSet, yestodayIdSet);
                diffCount[0] = oldColl.size();
                diffCount[1] = commonIdSet.size();
                diffCount[2] = newColl.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return diffCount;
    }

    public static Set<String> getIdSet(SpiderEnum spider, String city, String dateStr) {
        Set<String> todayIdSet = new HashSet<String>();
        File todayFile = new File(baseDir, dateStr + "/" + spider.name().toLowerCase() + ".txt");
        if (todayFile.isFile()) {
            try {
                List<String> sampleLines = FileUtils.readLines(todayFile, "UTF-8");
                for (int lineIndex = sampleLines.size() - 1; lineIndex >= 0; lineIndex--) {
                    String sampleLine = sampleLines.get(lineIndex);
                    CarVo carVo = JSON.parseObject(sampleLine, CarVo.class);
                    String srcId = carVo.getSrcId();
                    String carCity = carVo.getCity();
                    if (null != city && city.equals(carCity)) {
                        todayIdSet.add(srcId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return todayIdSet;
    }
}