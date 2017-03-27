package com.gaocy.sample.spider;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarVo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by godwin on 2017-03-16.
 */
public class SpiderRunner<T extends List<CarVo>> implements Callable {

    public static final ExecutorService es = Executors.newFixedThreadPool(ConfUtil.getString("init.src.list").split(",").length);
    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dfTime = new SimpleDateFormat("HHmmss");
    private Spider spider;

    public SpiderRunner(Spider spider) {
        this.spider = spider;
    }

    public void run() {
        es.submit(this);
    }

    @Override
    public T call() throws Exception {
        String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
        String[] cityNameArr = spider.getCityNameArr();
        T infoAllList = (T) new ArrayList<CarVo>();
        for (String cityName : cityNameArr) {
            long startTime = System.currentTimeMillis();
            List<CarVo> infoList = spider.listByCityName(cityName);
            long endTime = System.currentTimeMillis();
            String timeStr = dfDate.format(new Date(startTime)) + " " + dfTime.format(new Date(startTime)) + "_" + dfTime.format(new Date(endTime));
            SpiderBase.logToFile("elapse_" + dfDate.format(new Date()), "[" + timeStr + "] [" + spiderName + "] [" + cityName + "] get " + infoList.size() + " cars, elapse: " + ((endTime - startTime) / 1000) + "s");
            infoAllList.addAll(infoList);
        }
        callback(infoAllList);
        return infoAllList;
    }

    public void callback(List<CarVo> list) {
        for (CarVo vo : list) {
            // SpiderBase.logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name() + ".txt", JSON.toJSONString(vo));
        }
    }
}