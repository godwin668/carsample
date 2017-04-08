package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.BizVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailXinApp {

    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static DateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static File baseDir = new File(ConfUtil.getString("init.log.base.url"));

    // http://www.che168.com/dealer/125701/20443429.html?pvareaid=100519#pos=6#page=1#rtype=1
    protected static String BASE_URL_XIN = "http://www.xin.com";

    public static void main(String[] args) {
        String dateStr = "20170407";
        SpiderEnum spiderEnum = SpiderEnum.youxin;
        String[] cityArr = new String[] { "北京", "长沙", "重庆", "石家庄", "天津" };
        genAllShop();
    }

    public static void genAllShop() {
        for (int i = 1; i < 65000; i++) {
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
                    String city = shopTabElements.get(0).text();
                    String phone = shopTabElements.get(1).attr("data-mobile");
                    BizVo bizVo = new BizVo();
                    bizVo.setId("" + i);
                    bizVo.setCity(city);
                    bizVo.setName(name);
                    bizVo.setName(address);
                    bizVo.setPhone(phone);
                    bizVo.setUrl(url);
                    System.out.println("info: " + name + ", " + address + ", " + city + ", " + phone);
                    SpiderBase.logToFile("summary/" + SpiderEnum.youxin.name() + "_shop", dfDateTime.format(new Date()) + "\t" + city + "\t" + url + "\t" + name + "\t" + address + "\t" + phone);
                    SpiderBase.logToFile("summary/" + SpiderEnum.youxin.name() + "_shop_json", JSON.toJSONString(bizVo));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取列表页车源信息
     *
     * @param spider 抓取源
     * @param city 城市
     * @param dateStr 日期
     * @return
     */
    public static List<CarVo> listCarVo(SpiderEnum spider, String city, String dateStr) {
        List<CarVo> carVoList = new ArrayList<CarVo>();
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
}