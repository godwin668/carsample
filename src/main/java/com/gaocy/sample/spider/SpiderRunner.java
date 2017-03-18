package com.gaocy.sample.spider;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CityEnum;
import com.gaocy.sample.vo.InfoVo;

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
public class SpiderRunner<T extends List<InfoVo>> implements Callable {

    public static final ExecutorService es = Executors.newFixedThreadPool(ConfUtil.getString("init.src.list").split(",").length);
    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    private Spider spider;

    public SpiderRunner(Spider spider) {
        this.spider = spider;
    }

    public void run() {
        es.submit(this);
    }

    @Override
    public T call() throws Exception {
        CityEnum[] cityArr = spider.getCityArr();
        T infoAllList = (T) new ArrayList<InfoVo>();
        for (CityEnum city : cityArr) {
            List<InfoVo> infoList = spider.listByCity(city);
            infoAllList.addAll(infoList);
        }
        callback(infoAllList);
        return infoAllList;
    }

    public void callback(List<InfoVo> list) {
        for (InfoVo vo : list) {
            SpiderBase.logToFile("rawsample/" + vo.getSrc().name() + "/" + dfDate.format(new Date()), JSON.toJSONString(vo));
        }
    }
}