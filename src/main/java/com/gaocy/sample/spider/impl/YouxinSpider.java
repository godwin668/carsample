/*
 * Copyright (c) I Doc View. 北京卓软在线信息技术有限公司. All rights reserved.
 * 项目名称：I Doc View在线文档预览系统
 * 文件名称：YouxinStat.java
 * Date：20150101
 * Author: godwin
 */

package com.gaocy.sample.spider.impl;

import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.SenderUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.vo.InfoVo;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-02.
 */
public class YouxinSpider extends SpiderBase implements Spider {

    private static String baseUrl = "http://www.xin.com/<city>/s/i<page>/";

    public YouxinSpider(String[] cityArr) {
        super(cityArr);
    }

    /**
     * get all info in current city
     *
     * @param city
     * @return
     */
    @Override
    public List<InfoVo> listByCity(String city) {
        List<InfoVo> infoList = new ArrayList<InfoVo>();
        String url = baseUrl.replaceFirst("<city>", city);
        String[] mileageUriSubArr = {"sn_k0-1", "sn_k1-3", "sn_k3-6", "sn_k6-10", "sn_k10-20", "sn_k20-"};  // 里程
        for (String mileageUriSub : mileageUriSubArr) {                     // 循环所有里程
            String mileageUrl = url.replaceFirst("s", mileageUriSub);
            int pageCount = getPageCount(mileageUrl);
            for (int i = 1; i <= pageCount; i++) {
                String listUrl = url.replaceFirst("<page>", "" + i);
                Document doc = getDoc(listUrl);
                Elements infoElements = doc.select(".list-con").get(0).select(".con");
                if (null == infoElements || infoElements.size() < 1) {
                    SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listurl: " + listUrl + ", doc: " + doc);
                }
                for (Element infoElement : infoElements) {
                    try {
                        String infoHref = infoElement.select(".aimg").get(0).attr("href");
                        String hrefRegex = ".*?(\\d+).html";
                        String infoId = infoHref.replaceFirst(hrefRegex, "$1");



                        String infoName = infoElement.select(".list-infoBox .infoBox a").get(0).text();
                        String infoCity = infoHref.replaceFirst("/(\\w+)/(\\w+).htm", "$1");

                        String infoRegDate = infoElement.select(".list-infoBox .fc-gray span").get(0).text().replaceAll("上牌", "");
                        String infoMileageStr = infoElement.select(".list-infoBox .fc-gray").get(0).text();
                        String infoMileage = infoMileageStr.substring(infoMileageStr.indexOf("行驶") + 2);
                        String infoPrice = infoElement.select(".list-infoBox .priType-s .priType").get(0).text();

                        InfoVo vo = new InfoVo();
                        vo.setSrc(SpiderEnum.GUAZI);
                        vo.setCity(infoCity);
                        vo.setSrcId(infoId);
                        vo.setName(infoName);
                        vo.setRegDate(infoRegDate);
                        vo.setMileage(infoMileage);
                        vo.setPrice(infoPrice);
                        vo.setAddress(infoHref);
                        infoList.add(vo);
                        logToFile("guazi.txt", vo.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        logToFile("error.txt", e.toString());
                    }
                }
            }
        }
        return infoList;
    }

    public static int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 0;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".search_page_link a");
        if (null == pageLinkElements || pageLinkElements.size() < 1) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listurl: " + url + ", doc: " + doc);
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