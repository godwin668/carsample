package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailApp extends DetailBaseApp implements Callable {

    public static final ExecutorService es = Executors.newFixedThreadPool(10);

    private String dateStr;
    private SpiderEnum spiderEnum;
    private String[] cityArr;

    public DetailApp(String dateStr, SpiderEnum spiderEnum, String[] cityArr) {
        this.dateStr = dateStr;
        this.spiderEnum = spiderEnum;
        this.cityArr = cityArr;
    }

    public static void main(String[] args) {
        String dateStr = "20170415";
        SpiderEnum[] spiderEnumArr = { SpiderEnum.che168, SpiderEnum.youxin };
        String[] cityArr = new String[] { "北京", "长沙", "重庆", "石家庄", "天津" };
        for (SpiderEnum spider : spiderEnumArr) {
            DetailApp app = new DetailApp(dateStr, spider, cityArr);
            es.submit(app);
        }
    }

    @Override
    public Object call() throws Exception {
        for (String city : cityArr) {
            Spider spider = SpiderFactory.getSpider(spiderEnum, new String[] { city });
            String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
            List<CarVo> carVoList = listCarVo(spiderEnum, city, dateStr);
            System.out.println(city + "_" + carVoList.size());
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + " " + city + ", info size: " + carVoList.size());
            for (CarVo carVo : carVoList) {
                CarDetailVo carDetailVo = spider.getByUrl(carVo);
                if (null != carDetailVo && StringUtils.isNoneBlank(carDetailVo.getId())) {
                    SpiderBase.logToFile("cardetail/" + spiderName + "/" + city, JSON.toJSONString(carDetailVo));
                }
            }
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + " " + city + ", info size: " + carVoList.size());
        }
        return null;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public SpiderEnum getSpiderEnum() {
        return spiderEnum;
    }

    public void setSpiderEnum(SpiderEnum spiderEnum) {
        this.spiderEnum = spiderEnum;
    }

    public String[] getCityArr() {
        return cityArr;
    }

    public void setCityArr(String[] cityArr) {
        this.cityArr = cityArr;
    }
}