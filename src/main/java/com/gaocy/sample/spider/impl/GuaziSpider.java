package com.gaocy.sample.spider.impl;

import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.SenderUtil;
import com.gaocy.sample.vo.CityEnum;
import com.gaocy.sample.vo.CarVo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class GuaziSpider extends SpiderBase implements Spider {

    private static String baseUrl = "https://www.guazi.com/<city>/buy/o<page>/";

    public GuaziSpider(String[] cityArr) {
        super(cityArr);
    }

    /**
     * get all info in current city
     *
     * @param city
     * @return
     */
    @Override
    public List<CarVo> listByCity(CityEnum city) {
        List<CarVo> infoList = new ArrayList<CarVo>();
        String url = baseUrl.replaceFirst("<city>", city.getPy());
        int pageCount = getPageCount(url);
        pageCount = 2;
        for (int i = 1; i <= pageCount; i++) {
            String listUrl = url.replaceFirst("<page>", "" + i);
            Document doc = getDoc(listUrl);
            Elements infoElements = doc.select(".list ul li");
            if (null == infoElements || infoElements.size() < 1) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity: " + listUrl);
            }
            for (Element infoElement : infoElements) {
                try {
                    String infoName = infoElement.select(".list-infoBox .infoBox a").get(0).text();
                    String infoHref = infoElement.select(".list-infoBox .infoBox a").get(0).attr("href");
                    String infoCity = infoHref.replaceFirst("/(\\w+)/(\\w+).htm", "$1");
                    String infoId = infoHref.replaceFirst("/(\\w+)/(\\w+).htm", "$2");
                    String infoRegDateStr = infoElement.select(".list-infoBox .fc-gray span").get(0).text().replaceAll("上牌", "");
                    String infoRegDate = infoRegDateStr.replaceFirst("(\\d+)年(\\d+)月", "$1$2");
                    if (infoRegDate.length() == 5) {
                        infoRegDate = infoRegDate.replaceFirst("(\\d{4})(\\d{1})", "$10$2");
                    }
                    String infoMileageStr = infoElement.select(".list-infoBox .fc-gray").get(0).text();
                    String infoMileage = infoMileageStr.substring(infoMileageStr.indexOf("行驶") + 2);
                    String infoPrice = infoElement.select(".list-infoBox .priType-s .priType").get(0).text();

                    CarVo vo = new CarVo();
                    vo.setSrc(SpiderEnum.guazi);
                    vo.setCity(CityEnum.getByPY(infoCity).getPinyin());
                    vo.setSrcId(infoId);
                    vo.setName(infoName);
                    vo.setRegDate(infoRegDate);
                    vo.setMileage(infoMileage);
                    vo.setPrice(infoPrice);
                    vo.setAddress(infoHref);
                    infoList.add(vo);
                    logToFile("guazi", vo.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    logToFile("error", e.toString());
                }
            }
        }
        return infoList;
    }

    public static int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".pageBox .pageLink a span");
        if (null == pageLinkElements || pageLinkElements.size() < 1) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
        }
        for (Element pageLinkElement : pageLinkElements) {
            String pageData = pageLinkElement.text();
            if (null != pageData && pageData.matches("\\d+")) {
                int curPage = Integer.valueOf(pageData);
                if (curPage > pageCount) {
                    pageCount = curPage;
                }
            }
        }
        return pageCount;
    }
}