package com.gaocy.sample.spider.impl;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.SenderUtil;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by godwin on 2017-03-16.
 */
public class GuaziSpider extends SpiderBase implements Spider {

    private static String URL_LIST_TEMPLATE = "https://www.guazi.com/<city>/buy/o<page>/";

    public GuaziSpider(String[] cityArr) {
        super(cityArr);
    }

    /**
     * get all info in current city
     *
     * @param cityName
     * @return
     */
    @Override
    public List<CarVo> listByCityName(String cityName) {
        List<CarVo> infoList = new ArrayList<CarVo>();
        String cityEName = CityUtil.getEName(SpiderEnum.guazi, cityName);
        if (StringUtils.isBlank(cityEName)) {
            return infoList;
        }
        Set<String> idSet = new HashSet<String>();
        String url = URL_LIST_TEMPLATE.replaceFirst("<city>", cityEName);
        int pageCount = getPageCount(url);
        boolean isOtherCity = false;
        for (int i = 1; i <= pageCount; i++) {
            if (isOtherCity) {
                break;
            }
            String listUrl = url.replaceFirst("<page>", "" + i);
            Document doc = getDoc(listUrl);
            if (null == doc) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity doc: " + listUrl);
                continue;
            }
            Elements infoElements = doc.select(".carlist li");
            if (null == infoElements || infoElements.size() < 1) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity: " + listUrl);
            }
            for (Element infoElement : infoElements) {
                try {
                    String infoName = infoElement.select("a .t").get(0).text();
                    String infoHref = infoElement.select("a").get(0).attr("href");
                    String infoCity = infoHref.replaceFirst("/(\\w+)/(\\w+).htm", "$1");
                    String infoCityName = CityUtil.getName(SpiderEnum.guazi, infoCity);
                    if (!cityName.equals(infoCityName)) {
                        continue;
                    }
                    String infoId = infoHref.replaceFirst("/(\\w+)/(\\w+).htm", "$2");
                    String infoRegDateMileageStr = infoElement.select(".t-i").get(0).text().replaceAll("年", "").replaceAll("万公里", "");
                    String infoRegDate = infoRegDateMileageStr.split("\\|")[0];
                    String infoMileage = infoRegDateMileageStr.split("\\|")[1];
                    String infoPrice = infoElement.select(".t-price p").get(0).text().replaceAll("万", "");

                    CarVo vo = new CarVo();
                    vo.setSrc(SpiderEnum.guazi);
                    vo.setCity(CityUtil.getName(SpiderEnum.guazi, infoCity));
                    vo.setSrcId(infoId);
                    vo.setName(infoName);
                    vo.setRegDate(infoRegDate);
                    vo.setMileage(infoMileage);
                    vo.setPrice(infoPrice);
                    vo.setAddress(infoHref);
                    if (!idSet.contains(infoId)) {
                        infoList.add(vo);
                        logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase(), JSON.toJSONString(vo));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logToFile("error", e.toString());
                }
            }
        }
        return infoList;
    }

    public int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        try {
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
        } catch (Exception e) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
        }
        return pageCount;
    }
}