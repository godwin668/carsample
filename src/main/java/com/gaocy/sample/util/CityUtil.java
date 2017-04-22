package com.gaocy.sample.util;

import com.gaocy.sample.spider.SpiderEnum;
import org.apache.commons.configuration2.Configuration;

import java.util.*;

/**
 * Created by godwin on 2017-03-17.
 */
public class CityUtil {

    // 中文名称-pinyin
    private static Map<String, String> cityName2PinyinMap = new TreeMap<String, String>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            String py1 = PinyinUtil.getPinYin(o1);
            String py2 = PinyinUtil.getPinYin(o2);
            return py1.compareTo(py2);
        }
    });

    // pinyin-中文名称
    private static Map<String, String> cityPinyin2NameMap = new TreeMap<String, String>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    });

    // 城市中文名称-城市英文名称
    private static Map<SpiderEnum, Map<String, String>> spiderCityZH2ENMap = new HashMap<SpiderEnum, Map<String, String>>();

    // 城市英文名称-城市中文名称
    private static Map<SpiderEnum, Map<String, String>> spiderCityEN2ZHMap = new HashMap<SpiderEnum, Map<String, String>>();

    static {
        Set<String> cityNameSet = new LinkedHashSet<String>();
        for (SpiderEnum spider : SpiderEnum.values()) {
            Map<String, String> cityZH2ENMap = new LinkedHashMap<String, String>();
            spiderCityZH2ENMap.put(spider, cityZH2ENMap);
            Map<String, String> cityEN2ZHMap = new LinkedHashMap<String, String>();
            spiderCityEN2ZHMap.put(spider, cityEN2ZHMap);
            Configuration cityConf = ConfUtil.getConfByName("city/" + spider.name() + ".properties");
            Iterator<String> cityKeys = cityConf.getKeys();
            while (cityKeys.hasNext()) {
                String cityName = cityKeys.next();
                String cityValue = cityConf.getString(cityName);
                cityZH2ENMap.put(cityName, cityValue);
                cityEN2ZHMap.put(cityValue, cityName);
                cityNameSet.add(cityName);
            }
        }

        for (String cityName : cityNameSet) {
            String cityPinyin = PinyinUtil.getPinYin(cityName);
            cityName2PinyinMap.put(cityName, cityPinyin);
            cityPinyin2NameMap.put(cityPinyin, cityName);
        }
    }

    /**
     * 根据城市中文名称获取英文名称
     *
     * @param spider
     * @param cityName
     * @return
     */
    public static String getEName(SpiderEnum spider, String cityName) {
        try {
            return spiderCityZH2ENMap.get(spider).get(cityName);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据城市英文名称获取中文名称
     *
     * @param spider
     * @param cityEName
     * @return
     */
    public static String getName(SpiderEnum spider, String cityEName) {
        try {
            return spiderCityEN2ZHMap.get(spider).get(cityEName);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getNameByPinyin(String pinyin) {
        return cityPinyin2NameMap.get(pinyin);
    }

    public static String getPinyinByName(String name) {
        return cityName2PinyinMap.get(name);
    }

    public static Set<String> getAllCityName() {
        return cityName2PinyinMap.keySet();
    }

    public static Set<String> getAllCityNameBySpider(SpiderEnum spider) {
        return spiderCityZH2ENMap.get(spider).keySet();

    }

    public static void main(String[] args) {
        System.out.println(SpiderEnum.guazi + ": " + getEName(SpiderEnum.guazi, "北京"));
        System.out.println(SpiderEnum.guazi + ": " + getName(SpiderEnum.guazi, "bj"));

        System.out.println("pinyin: " + PinyinUtil.getPinYin("北京"));

        System.out.println("名称-pinyin: 北京_" + getPinyinByName("北京"));
        System.out.println("pinyin-名称: beijing_" + getNameByPinyin("beijing"));

        System.out.println("---------------------------------------------------");
        SpiderEnum[] spiders = SpiderEnum.values();
        for (SpiderEnum spider : spiders) {
            Set<String> citySet = getAllCityNameBySpider(spider);
            Set<String> sortedCitySet = new TreeSet<String>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String py1 = PinyinUtil.getPinYin(o1);
                    String py2 = PinyinUtil.getPinYin(o2);
                    return py1.compareTo(py2);
                }
            });
            sortedCitySet.addAll(citySet);
            System.out.println("----------------------------------------");
            System.out.println(spider.name());
            for (String sortedCityName : sortedCitySet) {
                String enName = getEName(spider, sortedCityName);
                System.out.println(sortedCityName + "=" + enName);
            }
        }
    }
}