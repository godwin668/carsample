package com.gaocy.sample.util;

import com.gaocy.sample.spider.SpiderEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by godwin on 2017-03-17.
 */
public class CityUtil {

    private static Map<SpiderEnum, Map<String, String>> spiderCityMap = new HashMap<SpiderEnum, Map<String, String>>();

    static {
        // 瓜子
        Map<String, String> guaziMap = new HashMap<String, String>();
        spiderCityMap.put(SpiderEnum.GUAZI, guaziMap);
        String[] guaziCityArr = ConfUtil.getString("city.map.guazi").split(",");
        for (String guaziCity : guaziCityArr) {
            String[] guaziCityKV = guaziCity.split("-");
            guaziMap.put(guaziCityKV[0], guaziCityKV[1]);
        }

        // 优信
        Map<String, String> youxinMap = new HashMap<String, String>();
        spiderCityMap.put(SpiderEnum.YOUXIN, youxinMap);
        String[] youxinCityArr = ConfUtil.getString("city.map.youxin").split(",");
        for (String youxinCity : youxinCityArr) {
            String[] youxinCityKV = youxinCity.split("-");
            youxinMap.put(youxinCityKV[0], youxinCityKV[1]);
        }
    }

    public static String get(SpiderEnum spider, String cityName) {
        try {
            return spiderCityMap.get(spider).get(cityName);
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        System.out.println(ConfUtil.getString("city.map.guazi"));

        System.out.println(SpiderEnum.GUAZI + ": " + get(SpiderEnum.GUAZI, "北京"));
        System.out.println(SpiderEnum.YOUXIN + ": " + get(SpiderEnum.YOUXIN, "北京"));

    }
}