package com.gaocy.sample.spider.impl;

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
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class Che168Spider extends SpiderBase implements Spider {

    private static String baseUrl = "http://www.che168.com/<city>/a0_0msdgscncgpi1ltocsp<page>exx0/";

    public Che168Spider(String[] cityArr) {
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
        String cityEName = CityUtil.getEName(SpiderEnum.che168, cityName);
        if (StringUtils.isBlank(cityEName)) {
            return infoList;
        }
        String url = baseUrl.replaceFirst("<city>", cityEName);
        int pageCount = getPageCount(url);
        String mileageTimeCityRegex = "(.*?)万公里／(.*?)／(.*)";
        for (int i = 1; i <= pageCount; i++) {
            String listUrl = url.replaceFirst("<page>", "" + i);
            Document doc = getDoc(listUrl);
            Elements infoElements = doc.select(".tab-content .list-photo ul li");
            if (null == infoElements || infoElements.size() < 1) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity: " + listUrl);
            }
            for (Element infoElement : infoElements) {
                try {
                    String infoName = infoElement.select(".list-photo-info h3").get(0).text();
                    String infoHref = infoElement.select("a").get(0).attr("href");
                    String mileageTimeCityStr = infoElement.select(".list-photo-info .time").get(0).text();   // 2万公里／2015-12／北京
                    String infoCity = mileageTimeCityStr.replaceFirst(mileageTimeCityRegex, "$3");
                    String infoId = infoHref.replaceFirst(".*?/(\\d+).html.*", "$1");
                    String infoRegDate = mileageTimeCityStr.replaceFirst(mileageTimeCityRegex, "$2").replaceAll("-", "");
                    String infoMileage = mileageTimeCityStr.replaceFirst(mileageTimeCityRegex, "$1");
                    String infoPrice = infoElement.select(".list-photo-info .price em b").get(0).text();

                    CarVo vo = new CarVo();
                    vo.setSrc(SpiderEnum.che168);
                    vo.setCity(CityUtil.getName(SpiderEnum.che168, infoCity));
                    vo.setSrcId(infoId);
                    vo.setName(infoName);
                    vo.setRegDate(infoRegDate);
                    vo.setMileage(infoMileage);
                    vo.setPrice(infoPrice);
                    vo.setAddress(infoHref);
                    infoList.add(vo);
                    logToFile("che168", vo.toString());
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
        Elements pageLinkElements = doc.select(".page a");
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