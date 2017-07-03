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
import java.text.ParseException;
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

    private static List<String> userPool = new ArrayList<String>();
    private static int userPoolIndex = 0;

    static {
        userPool.add("guaZiUserInfo=7MxTl3GksrpprE1NtNvd");
        userPool.add("guaZiUserInfo=fMxTl3Gihw8BaZZcA943e");
        userPool.add("guaZiUserInfo=eMxTl3GkDpHV9TZ4GrYDf");
    }

    public static String getUser() {
        userPoolIndex = ++userPoolIndex % userPool.size();
        return userPool.get(userPoolIndex);
    }

    public static void main(String[] args) {
        runDealSpider();
    }

    // @Scheduled(cron = "${init.spider.detail.cron}")
    public static void runDealSpider() {
        Map<String, Integer> idMap = new HashMap<String, Integer>();
        String startDateStr = "200001";
        Date endDate = null;
        try {
            endDate = dfMonth.parse("201001");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Date startDate = dfMonth.parse(startDateStr);
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.guazi_deal, "[ALL START] [" + dfDateTime.format(new Date()) + "] From " + dfDate.format(startDate) + " to " + dfDate.format(endDate));
            for (Date indexDate = startDate; indexDate.before(endDate); indexDate = DateUtils.addMonths(indexDate, 1)) {
                String indexDateStr = dfMonth.format(indexDate);
                Map<String, ModelVo> guaziSeriesEntityMap = GuaziModel.getSeriesMap();
                for (Map.Entry<String, ModelVo> entry : guaziSeriesEntityMap.entrySet()) {
                    int uniqueVoSize = 0;
                    String seriesId = entry.getKey();
                    List<CarVo> carVoList = getList(indexDateStr + "00", seriesId);
                    for (CarVo vo : carVoList) {
                        String srcId = vo.getSrcId();
                        Integer imgUrlCount = idMap.get(srcId);
                        if (null != imgUrlCount && imgUrlCount > 0) {
                            idMap.put(srcId, ++imgUrlCount);
                        } else {
                            ++uniqueVoSize;
                            SpiderBase.logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase(), JSON.toJSONString(vo));
                            idMap.put(srcId, 1);
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
        String user = getUser();
        headerMap.put("Cookie", user);
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + SpiderEnum.guazi_deal + "_url", userPoolIndex + "_" + user + "_" + url);

        // String content = HttpClientUtil.get(url, userPoolIndex, headerMap);
        // Document document = Jsoup.parse(content);

        Document document = SpiderBase.getDoc(url, headerMap);

        if (null == document){
            return list;
        }

        Elements carDocs = document.select(".deal-list li");
        if (null == carDocs){
            return list;
        }
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
            String srcId = img;
            srcId = srcId.replaceFirst(".*?\\.com/(.*?)\\.jpg.*", "$1");

            Map<String, String> params = new HashMap<String, String>();
            params.put("seriesId", seriesId);
            params.put("month", dateMontStr);

            CarVo vo = new CarVo();
            vo.setSrc(SpiderEnum.guazi_deal);
            vo.setName(modelName);
            // vo.setAddress(url.replaceFirst(".*?guazi\\.com(.*)", "$1"));
            vo.setSrcId(srcId);
            vo.setRegDate(year + "01");
            vo.setMileage(mileage);
            vo.setCity(city);
            vo.setPrice(dealPrice);
            vo.setParams(params);
            list.add(vo);
            // SpiderBase.logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase() + "_deal", JSON.toJSONString(vo));
        }
        // System.out.println(content);
        return list;
    }
}