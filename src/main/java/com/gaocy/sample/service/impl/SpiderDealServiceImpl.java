package com.gaocy.sample.service.impl;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.model.GuaziModel;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.HttpClientUtil;
import com.gaocy.sample.vo.CarVo;
import com.gaocy.sample.vo.ModelVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SpiderDealServiceImpl {

    private static String URL_LIST_TEMPLATE = "https://www.guazi.com/bj/dealrecord?tag_id=<seriesId>&date=<date>";

    private static DateFormat dfMonth = new SimpleDateFormat("yyyyMM");
    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        runDealSpider();
    }

    @Scheduled(cron = "${init.spider.detail.cron}")
    public static void runDealSpider() {
        Map<String, Integer> idMap = new HashMap<String, Integer>();
        String startDateStr = "201701";
        Date endDate = new Date();
        try {
            Date startDate = dfMonth.parse(startDateStr);
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.guazi_deal, "[ALL START] [" + dfDateTime.format(new Date()) + "] From " + dfDate.format(startDate) + " to " + dfDate.format(endDate));
            for (Date indexDate = startDate; indexDate.before(endDate); indexDate = DateUtils.addMonths(indexDate, 1)) {
                String indexDateStr = dfMonth.format(indexDate);
                Map<String, ModelVo> guaziSeriesEntityMap = GuaziModel.getSeriesMap();
                for (Map.Entry<String, ModelVo> entry : guaziSeriesEntityMap.entrySet()) {
                    int uniqueVoSize = 0;
                    String seriesId = entry.getKey();
                    ModelVo guaziSeriesEntity = entry.getValue();
                    List<CarVo> carVoList = getList(indexDateStr + "00", seriesId);
                    for (CarVo vo : carVoList) {
                        String imgUrl = vo.getAddress();
                        Integer imgUrlCount = idMap.get(imgUrl);
                        if (null != imgUrlCount && imgUrlCount > 0) {
                            idMap.put(imgUrl, ++imgUrlCount);
                        } else {
                            ++uniqueVoSize;
                            SpiderBase.logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase(), JSON.toJSONString(vo));
                            idMap.put(imgUrl, 1);
                        }
                    }
                    SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.guazi_deal, "[DETAIL] [" + dfDateTime.format(new Date()) + "] query:" + indexDateStr + "_" + seriesId + ", vo size(U/A):" + uniqueVoSize + "/" + carVoList.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.guazi_deal, "[ALL DONE] [" + dfDateTime.format(new Date()) + "] All car size: " + idMap.size());
    }

    public static List<CarVo> getList(String dateMontStr, String seriesId) {
        List<CarVo> list = new ArrayList<CarVo>();
        String url = URL_LIST_TEMPLATE.replaceFirst("<seriesId>", seriesId).replaceFirst("<date>", dateMontStr);
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Cookie", "guaZiUserInfo=aMxp18EGX6Z0%2B6xuv7Yf3");
        Document document = SpiderBase.getDoc(url, headerMap);
        Elements carDocs = document.select(".deal-list li");
        for (Element carDoc : carDocs) {
            String img = carDoc.select("img").get(0).attr("src");
            String modelName = carDoc.select(".deal-p1").text();
            String modelInfo = carDoc.select(".deal-p2").text();
            String modelInfoRegex = "(\\d+)年 \\| (.*?)万公里 \\| (.*)";
            String year = modelInfo.replaceFirst(modelInfoRegex, "$1");
            String mileage = modelInfo.replaceFirst(modelInfoRegex, "$2");
            String city = modelInfo.replaceFirst(modelInfoRegex, "$3");
            String dealPrice = carDoc.select(".deal-p3 em").text().replaceFirst("万", "");
            // System.out.println(modelName + "|" + year + "|" + mileage + "|" + city + "|" + dealPrice);

            if (StringUtils.isBlank(img)) {
                img = carDoc.select("img").get(0).attr("data-src");
                if (StringUtils.isBlank(img)) {
                    continue;
                }
            }

            CarVo vo = new CarVo();
            vo.setSrc(SpiderEnum.guazi_deal);
            vo.setName(modelName);
            vo.setAddress(img);
            vo.setRegDate(year + "01");
            vo.setMileage(mileage);
            vo.setCity(city);
            vo.setPrice(dealPrice);
            list.add(vo);
            // SpiderBase.logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase() + "_deal", JSON.toJSONString(vo));
        }
        // System.out.println(content);
        return list;
    }
}