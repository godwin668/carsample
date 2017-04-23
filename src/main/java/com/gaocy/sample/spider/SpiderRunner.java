package com.gaocy.sample.spider;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by godwin on 2017-03-16.
 */
public class SpiderRunner<T extends List<CarVo>> implements Callable {

    public static final ExecutorService es = Executors.newFixedThreadPool(10);
    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dfTime = new SimpleDateFormat("HHmmss");
    private Spider spider;

    public SpiderRunner(Spider spider) {
        this.spider = spider;
    }

    public void submit() {
        es.submit(this);
    }

    @Override
    public T call() throws Exception {
        String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
        String[] cityNameArr = spider.getCityNameArr();
        if (null == cityNameArr || cityNameArr.length < 1 || StringUtils.isBlank(cityNameArr[0])) {
            Set<String> citySet = CityUtil.getAllCityNameBySpider(SpiderEnum.valueOf(spiderName));
            if (null != citySet) {
                cityNameArr = citySet.toArray(new String[] {});
            }
        }

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