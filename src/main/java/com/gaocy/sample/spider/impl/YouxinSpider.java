/*
 * Copyright (c) I Doc View. 北京卓软在线信息技术有限公司. All rights reserved.
 * 项目名称：I Doc View在线文档预览系统
 * 文件名称：YouxinStat.java
 * Date：20150101
 * Author: godwin
 */

package com.gaocy.sample.spider.impl;

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

    protected static File logFile = new File("/Users/Godwin/car.log");
    private static DateFormat dfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    public YouxinSpider(String[] cityArr) {
        super(cityArr);
    }

    @Override
    public List<InfoVo> listByCity(String city) {
        return null;
    }

    public static void main(String[] args) {
        String baseUrl = "http://www.xin.com";
        String uri = "/beijing/s/i1/";  // sn_k0-1, 1-3, 3-6, 6-10, 10-20, 20-

        String[] cityUriSubArr = {"beijing", "shanghai", "guangzhou"};      // 城市
        String[] lileageUriSubArr = {"sn_k0-1", "sn_k1-3", "sn_k3-6", "sn_k6-10", "sn_k10-20", "sn_k20-"};  // 里程

        for (String cityUriSub : cityUriSubArr) {
            for (String lileageUriSub : lileageUriSubArr) {                     // 循环所有里程
                String url = baseUrl + uri;
                url = url.replaceFirst("s", lileageUriSub);
                url = url.replaceFirst("beijing", cityUriSub);
                int pageCount = getPageCount(url);
                System.out.println("max page: " + pageCount);

                List<Integer> carIdAllList = new ArrayList<Integer>();
                Set<Integer> carIdAllSet = new TreeSet<Integer>();

                for (int i = 1; i <= pageCount; i++) {                          // 循环所有页
                    String mileageUrl = url.replaceFirst("i1", "i" + i);
                    List<Integer> carIdList = getCarId(mileageUrl);
                    carIdAllList.addAll(carIdList);
                    carIdAllSet.addAll(carIdList);
                    // logToFile(uri, "page " + i);
                    // logToFile(uri, JSON.toJSONString(carIdList));
                    System.out.println("page " + i + " of " + pageCount + ", url: " + mileageUrl);
                    System.out.println(carIdList);
                }

                StringBuffer carIdAllStr = new StringBuffer();
                for (Integer carId : carIdAllSet) {
                    carIdAllStr.append("," + carId);
                }
                if (carIdAllStr.length() == 0) {
                    carIdAllStr.append(",");
                }
                logToFile("/xin/" + dfYYYYMMDD.format(new Date()) + "_" + cityUriSub + "_ids", carIdAllStr.substring(1));
                logToFile("/xin/" + dfYYYYMMDD.format(new Date()) + "_" + cityUriSub + "_sum", "" + carIdAllSet.size());
            }
        }
    }

    public static int getPageCount(String url) {
        int pageCount = 0;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".search_page_link a");
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

    public static List<Integer> getCarId(String url) {
        Document doc = getDoc(url);
        List<Integer> idList = new ArrayList<Integer>();
        Elements carElements = doc.select(".list-con").get(0).select(".con");
        for (Element carElement : carElements) {
            // System.out.println(carElement.text());
            Elements carIconElements = carElement.select(".ico1-new");
            String href = carElement.select(".aimg").get(0).attr("href");
            // System.out.println("href: " + href);
            String hrefRegex = ".*?(\\d+).html";
            if (null != href && href.matches(hrefRegex)) {
                String carId = href.replaceFirst(hrefRegex, "$1");
                idList.add(Integer.valueOf(carId));
            }
            /*
            if (null != carIconElements && carIconElements.size() > 0) {
                System.out.println("new car.");
            } else {
                System.out.println("old car");
            }
            */
        }
        return idList;
    }
}