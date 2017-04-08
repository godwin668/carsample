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
 * Created by godwin on 2017-03-02.
 */
public class YouxinSpider extends SpiderBase implements Spider {

    private static String URL_BASE = "http://www.xin.com";
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

    public CarDetailVo getByUrl(CarVo carVo) {
        CarDetailVo carDetailVo = new CarDetailVo();
        if (null == carVo) {
            return carDetailVo;
        }

        try {
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
            String color = detailDoc.select("#anchor02 .infotext-list li").get(3).text().replaceFirst(".*?：(.*)", "$1");
            carDetailVo.setColor(removeWhiteSpace(color));

            // 行驶里程 mileage
            Elements detailsElements = detailDoc.select(".details ul li");
            String mileage = detailsElements.get(0).text().replaceFirst("(.*?)万公里.*", "$1");
            carDetailVo.setMileage(mileage);

            // 排量
            String displacement = detailsElements.get(2).text().replaceFirst("(.*?)／(.*?)挡位.*", "$2");
            carDetailVo.setDisplacement(displacement);

            // 变速箱
            String gearBox = detailsElements.get(2).text().replaceFirst("(.*?)／(.*?)挡位.*", "$1");
            carDetailVo.setGearBox(gearBox);

            // 价格 price
            carDetailVo.setPrice(carVo.getPrice());

            // 发布时间 postDate
            String postDate = carAddressStr.replaceFirst(carAddressRegex, "$3").replaceAll("-", "").trim();
            carDetailVo.setPostDate(postDate);

            // 上牌时间 regDate
            String regDate = detailsElements.get(1).text().replaceAll("-", "").replaceFirst("(\\d+).*", "$1");
            carDetailVo.setRegDate(regDate);

            // 联系人 contact
            String userName = carAddressStr.replaceFirst(carAddressRegex, "$2").trim().replaceAll("&nbsp;", "");
            carDetailVo.setUserName(userName);

            // 联系电话 phone
            try {
                String phone = detailDoc.select(".car-results .btn-iphone3").get(0).text().replace("-", "").replaceFirst(".*?(\\d+).*", "$1");
                carDetailVo.setPhone(phone);
            } catch (Exception e) {
                logToFile("error", "[no phone] " + detailUrl);
            }

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
            carDetailVo.setIdentity(identity);

            // 商家名称 bizName
            String bizName = merchantsTitleElement.html().replaceAll("\n", "").replaceFirst ("(?s)(?i).*?\\|</i>([^<]+).*", "$1");
            bizName = removeWhiteSpace(bizName);
            carDetailVo.setBizName(bizName);

            // 商家id bizId
            String bizId = detailUrl.replaceFirst(".*?dealer/(\\d+).*", "$1");
            carDetailVo.setBizId(bizId);

            // 帖子状态 status
            // TODO

            // 帖子标签 tag
            // TODO
        } catch (Exception e) {
            logToFile("error", "[CAR VO] " + JSON.toJSONString(carVo));
        }
        return carDetailVo;
    }

    public int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        Document doc = getDoc(url);
        Elements pageLinkElements = doc.select(".search_page_link a");
        if (null == pageLinkElements || pageLinkElements.size() < 1) {
            pageLinkElements = doc.select(".con-page a");   // 优信店铺, e.g. http://www.xin.com/d/500.html
            if (null == pageLinkElements || pageLinkElements.size() < 1) {
                SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
            }
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