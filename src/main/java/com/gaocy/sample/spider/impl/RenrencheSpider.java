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
 * Created by godwin on 2017-03-16.
 */
public class RenrencheSpider extends SpiderBase implements Spider {

    private static String baseUrl = "https://www.renrenche.com/<city>/ershouche/p<page>";

    public RenrencheSpider(String[] cityArr) {
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
        String cityEName = CityUtil.getEName(SpiderEnum.renrenche, cityName);
        if (StringUtils.isBlank(cityEName)) {
            return infoList;
        }
        String url = baseUrl.replaceFirst("<city>", cityEName);
        int pageCount = getPageCount(url);
        boolean isOtherCity = false;
        String mileageTimeCityRegex = "(\\d+)年(\\d+)月/(.*?)万公里";    // 2010年03月/3.75万公里
        for (int i = 1; i <= pageCount; i++) {
            if (isOtherCity) {
                break;
            }
            String listUrl = url.replaceFirst("<page>", "" + i);
            Document doc = getDoc(listUrl);
            Elements infoElements = doc.select(".jscroll-inner .container ul li");
            if (null == infoElements || infoElements.size() < 1) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity: " + listUrl);
            }
            for (Element infoElement : infoElements) {
                try {
                    Elements infoNameElements = infoElement.select("a h3");
                    if (null == infoNameElements || infoNameElements.size() < 1) {
                        continue;
                    }
                    String infoName = infoElement.select("a h3").get(0).text();
                    String infoHref = infoElement.select("a").get(0).attr("href");
                    String infoCity = infoHref.replaceFirst("/(\\w+)/.*", "$1");
                    String infoId = infoHref.replaceFirst("/(\\w+)/car/(\\w+)", "$2");
                    String infoRegDateMileageStr = infoElement.select("a .mileage .basic").get(0).text();
                    String infoRegDate = infoRegDateMileageStr.replaceFirst(mileageTimeCityRegex, "$1$2");
                    if (infoRegDate.length() != 5 && infoRegDate.length() != 6) {
                        isOtherCity = true;
                        break;
                    }
                    if (infoRegDate.length() == 5) {
                        infoRegDate = infoRegDate.replaceFirst("(\\d{4})(\\d{1})", "$10$2");
                    }
                    String infoMileage = infoRegDateMileageStr.replaceFirst(mileageTimeCityRegex, "$3");
                    String infoPrice = infoElement.select(".tags-box .price").get(0).text().replaceAll("万", "");

                    CarVo vo = new CarVo();
                    vo.setSrc(SpiderEnum.renrenche);
                    vo.setCity(CityUtil.getName(SpiderEnum.renrenche, infoCity));
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
        return infoList;
    }

    public static int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".pagination li a");
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