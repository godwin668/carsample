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

import java.util.*;

/**
 * Created by godwin on 2017-03-16.
 */
public class Che168Spider extends SpiderBase implements Spider {

    // private static String URL_LIST_TEMPLATE = "http://www.che168.com/<city>/a0_0msdgscncgpi1ltocsp<page>exx0/";

    private static String URL_BASE = "http://www.che168.com";
    // private static String URL_LIST_TEMPLATE = URL_BASE + "/<city>/a0_0msdgscncgpi1ltocsp<page>exb104y96x0/";   // 20170405
    private static String URL_LIST_TEMPLATE = URL_BASE + "/<city>/<price>/a0_0msdgscncgpi1ltocsp<page>exx0/";   // 20170421

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
        Set<String> idSet = new HashSet<String>();
        String url = URL_LIST_TEMPLATE.replaceFirst("<city>", cityEName);
        String[] priceUriSubArr = { "0_3", "3_5", "5_8", "8_10", "10_15", "15_20", "20_30", "30_50", "50_0" };  // 价格
        for (String priceUriSub : priceUriSubArr) {
            String priceUrl = url.replaceFirst("<price>", priceUriSub);
            int pageCount = getPageCount(priceUrl);
            String mileageTimeCityRegex = "(.*?)万公里／(.*?)／(.*)";
            for (int i = 1; i <= pageCount; i++) {
                String listUrl = priceUrl.replaceFirst("<page>", "" + i);
                Document doc = getDoc(listUrl);
                if (null == doc) {
                    SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity doc: " + listUrl);
                    continue;
                }
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
                        if (!cityName.equals(infoCity)) {
                            continue;
                        }
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
                        Map<String, String> paramMap = new HashMap<String, String>();
                        String cpcCars = infoElement.attr("cpcpars");
                        if (null != cpcCars && StringUtils.isNotBlank(cpcCars)) {
                            paramMap.put("cpc", "1");
                            vo.setParams(paramMap);
                        }
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
            String[] brandNavArr = detailDoc.select(".breadnav").text().replaceAll("二手车", "").replaceAll("二手", "").replaceAll(cityName, "").split(" *> *");

            String carAddressStr = detailDoc.select(".car-address").text().replaceAll("&nbsp;", "").trim();
            carAddressStr = removeWhiteSpace(carAddressStr);
            String carAddressRegexDealer = "看车地点:(.*?)联系人:(.*?)发布时间:(.*)";
            String carAddressRegexPersonal = "看车地点:(.*?)发布时间:(.*)";

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
            String brandName = brandNavArr[brandNavArr.length - 3];
            carDetailVo.setBrandName(brandName);

            // 车系ID seriesId
            String seriesId = detailDoc.select("#car_seriesid").attr("value");                             // 车系ID
            carDetailVo.setSeriesId(seriesId);

            // 车系名称 seriesName
            String seriesName = brandNavArr[brandNavArr.length - 2];                                      // 车系名称
            carDetailVo.setSeriesName(seriesName);

            // 车型ID modelId
            String modelId = breadNavDoc.get(breadNavDoc.size() - 1).attr("href").replaceFirst(".*?/s(\\d+)/.*", "$1");       // 车型ID
            carDetailVo.setModelId(modelId);

            // 车型名称 modelName
            String modelName = brandNavArr[brandNavArr.length - 1];                          // 车型名称
            carDetailVo.setModelName(modelName);

            // 身份类型 identity
            String identity = "";
            if (detailUrl.contains("dealer")) {
                identity = "商家";
            } else if (detailUrl.contains("personal")) {
                identity = "个人";
            }
            carDetailVo.setIdentity(identity);

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
            if ("商家".equals(identity)) {
                String postDate = carAddressStr.replaceFirst(carAddressRegexDealer, "$3").replaceAll("-", "").trim();
                carDetailVo.setPostDate(postDate);
            } else if ("个人".equals(identity)) {
                String postDate = carAddressStr.replaceFirst(carAddressRegexPersonal, "$2").replaceAll("-", "").trim();
                carDetailVo.setPostDate(postDate);
            }

            // 上牌时间 regDate
            String regDate = detailsElements.get(1).text().replaceAll("-", "").replaceFirst("(\\d+).*", "$1");
            carDetailVo.setRegDate(regDate);

            // 联系人 contact
            if ("商家".equals(identity)) {
                String userName = carAddressStr.replaceFirst(carAddressRegexDealer, "$2").trim().replaceAll("&nbsp;", "");
                carDetailVo.setUserName(userName);
            } else if ("个人".equals(identity)) {
                String userName = detailDoc.select(".user-info .user-title").get(0).text();
                if (userName.contains("|")) {
                    userName = userName.substring(userName.indexOf("|") + 1);
                }
                carDetailVo.setUserName(userName);
            }

            // 联系电话 phone
            try {
                String phone = detailDoc.select(".car-results .btn-iphone3").get(0).text().replace("-", "").replaceFirst(".*?(\\d+).*", "$1");
                carDetailVo.setPhone(phone);
            } catch (Exception e) {
                logToFile("error", "[no phone] " + detailUrl);
            }

            // 看车地址 address
            if ("商家".equals(identity)) {
                String address = carAddressStr.replaceFirst(carAddressRegexDealer, "$1").trim();
                carDetailVo.setAddress(address);
            } else if ("个人".equals(identity)) {
                String address = carAddressStr.replaceFirst(carAddressRegexPersonal, "$1").trim();
                carDetailVo.setAddress(address);
            }

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

            // 商家名称 shopName
            try {
                if ("商家".equals(identity)) {
                    String shopName = detailDoc.select(".merchants-info .merchants-title").get(0).html().replaceAll("\n", "").replaceFirst ("(?s)(?i).*?\\|</i>([^<]+).*", "$1");
                    shopName = removeWhiteSpace(shopName);
                    carDetailVo.setShopName(shopName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 商家id shopId
            String shopId = "";
            if ("商家".equals(identity)) {
                shopId = detailUrl.replaceFirst(".*?dealer/(\\d+).*", "$1");
                carDetailVo.setShopId(shopId);
            }

            // 帖子状态 status
            // TODO

            // 帖子标签 tag
            List<String> tags = new ArrayList<String>();
            Elements goldMedalElements = detailDoc.select(".tag-gold-medal");   // 金牌店铺
            if (null != goldMedalElements && goldMedalElements.size() > 0) {
                tags.add("金牌店铺");
            }
            Elements tagElements = detailDoc.select(".car-price .tag");
            if (null != tagElements && tagElements.size() > 0) {
                for (Element tagElement : tagElements) {
                    tags.add(tagElement.text());
                }
            }
            Elements financeElements = detailDoc.select(".car-finance .mark");
            if (null != financeElements && financeElements.size() > 0) {
                if (financeElements.toString().contains("分期购")) {
                    tags.add("分期购");
                }
            }
            Elements commitmentTagElements = detailDoc.select(".commitment-tag ul li span");
            if (null != commitmentTagElements && commitmentTagElements.size() > 0) {
                for (Element commitmentTagElement : commitmentTagElements) {
                    tags.add(commitmentTagElement.text());
                }
            }

            if (tags.size() > 0) {
                carDetailVo.setTags(tags);
            }

            Map<String, String> paramMap = new HashMap<String, String>();
            Map<String, String> carVoParams = carVo.getParams();
            if (null != carVoParams && carVoParams.size() > 0) {
                paramMap.putAll(carVoParams);
            }
            Elements basicInfoElements = detailDoc.select("#anchor01 ul li");
            if (null != basicInfoElements && basicInfoElements.size() > 0) {
                for (Element basicInfoElement : basicInfoElements) {
                    try {
                        String key = basicInfoElement.text().replaceFirst("(.*?)：(.*)", "$1");
                        String value = basicInfoElement.text().replaceFirst("(.*?)：(.*)", "$2");
                        if (null != value && value.length() < 30) {
                            paramMap.put(removeWhiteSpace(key), removeWhiteSpace(value));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            carDetailVo.setParams(paramMap);

            // 商铺信息
            String url = "http://www.che168.com/Handler/GetOtherInfo.ashx?DealerId=" + shopId;
            boolean isGrabDealerInfo = false;
            if (isGrabDealerInfo && "商家".equals(identity) && StringUtils.isNotBlank(shopId)) {
                Map shopInfoMap = JSON.parseObject(SpiderBase.getDoc(url).text(), HashMap.class);
                carDetailVo.setShopInfo(shopInfoMap);
            }
        } catch (Exception e) {
            logToFile("error", "[CAR VO] " + JSON.toJSONString(carVo));
        }
        return carDetailVo;
    }

    public int getPageCount(String url) {
        url = url.replaceFirst("<page>", "1");
        int pageCount = 1;
        try {
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
        } catch (Exception e) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
        }
        return pageCount;
    }
}