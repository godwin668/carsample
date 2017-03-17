package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.*;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.InfoVo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by godwin on 2017-03-16.
 */
public class App {

    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) {
        String[] srcArr = ConfUtil.getString("init.src.list").split(",");
        String[] cityArr = ConfUtil.getString("init.city.list").split(",");
        List<Spider> spiderList = new ArrayList<Spider>();
        for (String src : srcArr) {
            Spider spider = SpiderFactory.getSpider(SpiderEnum.GUAZI, cityArr);
            SpiderRunner runner = new SpiderRunner(spider);
            runner.run();
        }
    }
}