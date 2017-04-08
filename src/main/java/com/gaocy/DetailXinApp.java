package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.vo.BizVo;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Date;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailXinApp extends DetailBaseApp {

    public static void main(String[] args) {
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
}