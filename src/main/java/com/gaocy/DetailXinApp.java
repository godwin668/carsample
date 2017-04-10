package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.ShopVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailXinApp extends DetailBaseApp {

    public static void main(String[] args) {
        // shop2CarDetail();
        String dateStr = "20170407";
        SpiderEnum spiderEnum = SpiderEnum.youxin;
        String[] cityArr = new String[] { "北京", "长沙", "重庆", "石家庄", "天津" };
        for (String city : cityArr) {
            Spider spider = SpiderFactory.getSpider(spiderEnum, new String[] { city });
            String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
            List<CarVo> carVoList = listCarVo(spiderEnum, city, dateStr);
            System.out.println(city + "_" + carVoList.size());
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + " " + city + ", info size: " + carVoList.size());
            for (CarVo carVo : carVoList) {
                CarDetailVo carDetailVo = spider.getByUrl(carVo);
                if (null != carDetailVo && StringUtils.isNoneBlank(carDetailVo.getId())) {
                    SpiderBase.logToFile("cardetail/" + spiderName + "/" + city, JSON.toJSONString(carDetailVo));
                }
            }
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + " " + city + ", info size: " + carVoList.size());
        }
    }

    /**
     * 从店铺入口->店铺列表页->车辆详情
     */
    public static void shop2CarDetail() {
        boolean SWITCH_GRAB_SHOP = false;
        boolean SWITCH_CAR_LIST = false;
        boolean SWITCH_CAR_DETAIL = false;

        // 抓取所有店铺信息
        if (SWITCH_GRAB_SHOP) {
            grabAllShop(null);
        }

        // 读取指定城市店铺信息
        String[] cityArr = new String[] { "北京", "长沙", "重庆", "石家庄", "天津" };
        if (null == cityArr) {
            cityArr = CityUtil.getAllCityNameBySpider(SpiderEnum.youxin).toArray(new String[] {});
        }
        List<ShopVo> shopList = listShop(cityArr);

        // 获取指定店铺车源列表
        Spider spider = SpiderFactory.getSpider(SpiderEnum.youxin, cityArr);
        String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
        if (SWITCH_CAR_LIST) {
            for (ShopVo shopVo : shopList) {
                String shopId = shopVo.getId();
                List<CarVo> shopCarList = spider.listByShopId(shopId);
                SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.youxin.name(), "[SHOP CARLIST] [" + dfDateTime.format(new Date()) + "] [" + shopVo.getCity() + "] " + shopVo.getName() + "(" + shopId + ") has " + shopCarList.size() + "(" + shopVo.getCarSum() + ") cars.");
            }
        }

        // 根据车源列表信息获取车源详情
        String dateStr = dfDate.format(new Date());
        if (SWITCH_CAR_DETAIL) {
            for (String city : cityArr) {
                List<CarVo> carVoList = listCarVoShop(SpiderEnum.youxin, city, dateStr);
                SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + " " + city + ", info size: " + carVoList.size());
                for (CarVo carVo : carVoList) {
                    CarDetailVo carDetailVo = spider.getByUrl(carVo);
                    SpiderBase.logToFile("cardetail/" + spiderName + "/" + city, JSON.toJSONString(carDetailVo));
                }
                SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + " " + city + ", info size: " + carVoList.size());
            }
        }
    }

    /**
     * 读取店铺信息
     *
     * @param cityArr
     * @return
     */
    public static List<ShopVo> listShop(String[] cityArr) {
        List<ShopVo> shopVoList = new ArrayList<ShopVo>();
        List<String> cityList = new ArrayList<String>();
        if (null != cityArr && cityArr.length > 0) {
            for (String city : cityArr) {
                if (StringUtils.isNotBlank(city)) {
                    cityList.add(city);
                }
            }
        }
        File shopFile = new File(baseDir, SpiderEnum.youxin.name() + "_shop.txt");
        if (!shopFile.isFile()) {
            return shopVoList;
        }
        try {
            List<String> shopStrList = FileUtils.readLines(shopFile, "UTF-8");
            for (String shopStr : shopStrList) {
                ShopVo shopVo = JSON.parseObject(shopStr, ShopVo.class);
                String shopCity =  shopVo.getCity();
                if (cityList.size() < 1 || cityList.contains(shopCity)) {
                    shopVoList.add(shopVo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shopVoList;
    }

    /**
     * 读取车源列表
     *
     * @param spider
     * @param city
     * @param dateStr
     * @return
     */
    public static List<CarVo> listCarVoShop(SpiderEnum spider, String city, String dateStr) {
        List<CarVo> carVoList = new ArrayList<CarVo>();
        File todayFile = new File(baseDir, dateStr + "/" + spider.name().toLowerCase() + "_shop.txt");
        if (todayFile.isFile()) {
            try {
                List<String> sampleLines = FileUtils.readLines(todayFile, "UTF-8");
                for (int lineIndex = sampleLines.size() - 1; lineIndex >= 0; lineIndex--) {
                    String sampleLine = sampleLines.get(lineIndex);
                    CarVo carVo = JSON.parseObject(sampleLine, CarVo.class);
                    String srcId = carVo.getSrcId();
                    String carCity = carVo.getCity();
                    if (null != city && city.equals(carCity)) {
                        carVoList.add(carVo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(carVoList, new Comparator<CarVo>() {
            @Override
            public int compare(CarVo o1, CarVo o2) {
                String id1 = o1.getSrcId();
                String id2 = o2.getSrcId();
                return id1.compareTo(id2);  // 递增
            }
        });
        return carVoList;
    }

    /**
     * 抓取所有店铺
     */
    public static void grabAllShop(List<String> shopIdList) {
        if (null == shopIdList || shopIdList.size() < 1) {
            shopIdList = new ArrayList<String>();
            for (int i = 1; i < 65000; i++) {
                shopIdList.add("" + i);
            }
        }
        for (String i : shopIdList) {
            try {
                String url = "http://www.xin.com/d/" + i + ".html";
                Document doc = SpiderBase.getDoc(url);
                int length = doc.toString().length();
                if (length < 2000) {
                    continue;
                }
                Elements shopTitleElements = doc.select(".shop-nav .shop-title");
                Elements shopTabElements = doc.select(".shop-nav .fr .shop-tab .tab-key");
                if (((null != shopTitleElements) && (shopTitleElements.size() > 0)) && ((null != shopTabElements) && (shopTabElements.size() > 1))) {
                    String name = shopTitleElements.select(".name").get(0).text();
                    String address = shopTitleElements.select(".ads").get(0).text();
                    String city = shopTabElements.get(shopTabElements.size() - 2).text();
                    String phone = shopTabElements.get(shopTabElements.size() - 1).attr("data-mobile");

                    ShopVo shopVo = new ShopVo();
                    shopVo.setId("" + i);
                    shopVo.setCity(city);
                    shopVo.setName(name);
                    shopVo.setAddress(address);
                    shopVo.setPhone(phone);
                    shopVo.setUrl(url);
                    try {
                        String carSum = doc.select(".shop-con .car-upper span em").text();
                        shopVo.setCarSum(Integer.valueOf(carSum));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SpiderBase.logToFile(SpiderEnum.youxin.name() + "_shop", JSON.toJSONString(shopVo));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}