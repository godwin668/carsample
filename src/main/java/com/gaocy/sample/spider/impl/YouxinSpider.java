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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by godwin on 2017-03-02.
 */
public class YouxinSpider extends SpiderBase implements Spider {

    private static String URL_LIST_TEMPLATE = "http://www.xin.com/<city>/s/i<page>/";

    public YouxinSpider(String[] cityArr) {
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
        String cityEName = CityUtil.getEName(SpiderEnum.youxin, cityName);
        if (StringUtils.isBlank(cityEName)) {
            return infoList;
        }
        String url = URL_LIST_TEMPLATE.replaceFirst("<city>", cityEName);
        String[] mileageUriSubArr = {"sn_k0-1", "sn_k1-3", "sn_k3-6", "sn_k6-10", "sn_k10-20", "sn_k20-"};  // 里程
        String regDateAndMileageRegex = "上牌(.*?)｜里程(.*?)万公里";
        for (String mileageUriSub : mileageUriSubArr) {                     // 循环所有里程
            String mileageUrl = url.replaceFirst("/s/", "/" + mileageUriSub + "/");
            int pageCount = getPageCount(mileageUrl);
            for (int i = 1; i <= pageCount; i++) {
                String mileagePageUrl = mileageUrl.replaceFirst("<page>", "" + i);
                Document doc = getDoc(mileagePageUrl);
                Elements infoElements = doc.select(".list-con").get(0).select(".con");
                if (null == infoElements || infoElements.size() < 1) {
                    SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity: " + mileagePageUrl);
                }
                for (Element infoElement : infoElements) {
                    try {
                        Element infoPadElement = infoElement.select(".pad").get(0);
                        Element titleElement = infoPadElement.select(".tit").get(0);
                        String infoHref = titleElement.attr("href");
                        String infoName = titleElement.text();
                        String infoId = titleElement.attr("data-carid");
                        String infoCity = infoHref.replaceFirst("/(\\w+)/(\\w+).html", "$1");
                        String regDateAndMileageStr = infoPadElement.select("span").get(0).text();
                        String infoRegDate = regDateAndMileageStr.replaceFirst(regDateAndMileageRegex, "$1").replaceFirst("/", "");
                        String infoMileage = regDateAndMileageStr.replaceFirst(regDateAndMileageRegex, "$2");
                        String infoPriceStr = infoPadElement.select("p").get(0).select("em").get(0).text();
                        String infoPrice = infoPriceStr.replaceFirst("(.*?)万.*", "$1");

                        CarVo vo = new CarVo();
                        vo.setSrc(SpiderEnum.youxin);
                        vo.setCity(CityUtil.getName(SpiderEnum.youxin, infoCity));
                        vo.setSrcId(infoId);
                        vo.setName(infoName);
                        vo.setRegDate(infoRegDate);
                        vo.setMileage(infoMileage);
                        vo.setPrice(infoPrice);
                        vo.setAddress(infoHref);
                        infoList.add(vo);
                        logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase(), JSON.toJSONString(vo));
                    } catch (Exception e) {
                        e.printStackTrace();
                        logToFile("error", e.toString());
                    }
                }
            }
        }
        return infoList;
    }

    public static int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".search_page_link a");
        if (null == pageLinkElements || pageLinkElements.size() < 1) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
        }
        for (Element pageLinkElement : pageLinkElements) {
            String pageData = pageLinkElement.attr("data-page");
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