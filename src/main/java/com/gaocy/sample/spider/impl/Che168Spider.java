package com.gaocy.sample.spider.impl;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.SenderUtil;
import com.gaocy.sample.vo.CarDetailVo;
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
public class Che168Spider extends SpiderBase implements Spider {

    // private static String URL_LIST_TEMPLATE = "http://www.che168.com/<city>/a0_0msdgscncgpi1ltocsp<page>exx0/";

    private static String URL_BASE = "http://www.che168.com";
    private static String URL_LIST_TEMPLATE = URL_BASE + "/<city>/a0_0msdgscncgpi1ltocsp<page>exb104y96x0/";   // 20170405

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
        String url = URL_LIST_TEMPLATE.replaceFirst("<city>", cityEName);
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
                    vo.setCity(infoCity);
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

    public CarDetailVo getByUrl(CarVo carVo) {
        CarDetailVo carDetailVo = new CarDetailVo();
        if (null == carVo) {
            return carDetailVo;
        }

        String detailUrl = URL_BASE + carVo.getAddress();
        Document detailDoc = getDoc(detailUrl);
        Elements breadNavDoc = detailDoc.select(".breadnav a");
        String cityName = breadNavDoc.get(1).text();

        String carAddressStr = detailDoc.select(".car-address").text().replaceAll("&nbsp;", "").trim();
        carAddressStr = removeWhiteSpace(carAddressStr);
        String carAddressRegex = "看车地点:(.*?)联系人:(.*?)发布时间:(.*)";

        Element merchantsTitleElement = detailDoc.select(".merchants-info .merchants-title").get(0);

        // 平台来源 src
        carDetailVo.setSrc(SpiderEnum.che168.name());

        // 城市 city
        carDetailVo.setCity(carVo.getCity());

        // 帖子ID id
        carDetailVo.setId(carVo.getSrcId());

        // 原帖链接 url
        carDetailVo.setUrl(carVo.getAddress());

        // 名称 name
        carDetailVo.setName(carVo.getName());

        // 品牌ID brandId
        String brandId = detailDoc.select("#car_brandid").attr("value");
        carDetailVo.setBrandId(brandId);

        // 品牌名称 brandName
        String brandName = breadNavDoc.get(2).text().replace("二手", "");
        carDetailVo.setBrandName(brandName);

        // 车系ID seriesId
        String seriesId = detailDoc.select("#car_seriesid").attr("value");                             // 车系ID
        carDetailVo.setBrandId(seriesId);

        // 车系名称 seriesName
        String seriesName = breadNavDoc.get(3).text().replace("二手", "");                            // 车系名称
        carDetailVo.setSeriesName(seriesName);

        // 车型ID modelId
        String modelId = breadNavDoc.get(4).attr("href").replaceFirst(".*?/s(\\d+)/.*", "$1");       // 车型ID
        carDetailVo.setModelId(modelId);

        // 车型名称 modelName
        String modelName = breadNavDoc.get(4).text().replace("二手", "");                             // 车型名称
        carDetailVo.setModelName(modelName);

        // 车辆颜色 color


        // 行驶里程 mileage
        carDetailVo.setMileage(carVo.getMileage());

        // 价格 price
        carDetailVo.setPrice(carVo.getPrice());

        // 发布时间 postDate
        String postDate = carAddressStr.replaceFirst(carAddressRegex, "$3").replaceAll("-", "").trim();
        carDetailVo.setUserName(postDate);

        // 上牌时间 regDate


        // 联系人 contact
        String userName = carAddressStr.replaceFirst(carAddressRegex, "$2").trim().replaceAll("&nbsp;", "");
        carDetailVo.setUserName(userName);

        // 联系电话 phone


        // 看车地址 address
        String address = carAddressStr.replaceFirst(carAddressRegex, "$1").trim();
        carDetailVo.setAddress(address);

        // 图片 images
        Elements imageElements = detailDoc.select(".fc-piclist ul li img");
        if (null != imageElements && imageElements.size() > 0) {
            List<String> imageUrlList = new ArrayList<String>();
            for (Element imageElement : imageElements) {
                String imageUrl = imageElement.attr("src2");
                imageUrlList.add(imageUrl);
            }
            carDetailVo.setImages(imageUrlList);
        }

        // 身份类型 identity
        String identity = merchantsTitleElement.select(".name").text();

        // 商家名称 bizName
        String bizName = merchantsTitleElement.html().replaceAll("\n", "").replaceFirst("(?s)(?i).*?</i>([^<]+)<span.*", "$1");
        bizName = removeWhiteSpace(bizName);
        carDetailVo.setBizName(bizName);

        // 商家id bizId
        String bizId = detailUrl.replaceFirst(".*?dealer/(\\d+).*", "$1");
        carDetailVo.setBizId(bizId);

        // 帖子状态 status
        // TODO

        // 帖子标签 tag
        // TODO

        return carDetailVo;
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