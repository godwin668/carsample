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
 * Created by godwin on 2017-03-02.
 */
public class YouxinSpider extends SpiderBase implements Spider {

    private static String URL_BASE = "http://www.xin.com";
    private static String URL_LIST_TEMPLATE = "http://www.xin.com/<city>/s/i<page>/";
    private static String URL_SHOP_TEMPLATE = "http://www.xin.com/d/<shopId>.html";

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
        Set<String> idSet = new HashSet<String>();
        String url = URL_LIST_TEMPLATE.replaceFirst("<city>", cityEName);
        String[] mileageUriSubArr = { "sn_k0-1", "sn_k1-3", "sn_k3-6", "sn_k6-10", "sn_k10-20", "sn_k20-" };  // 里程
        // String[] halfPayUriSubArr = { "", "v5" };    // v5: 付一半
        String[] halfPayUriSubArr = { "" };
        String regDateAndMileageRegex = "上牌(.*?)｜里程(.*?)万公里";
        for (String mileageUriSub : mileageUriSubArr) {                     // 循环所有里程
            String mileageUrl = url.replaceFirst("/s/", "/" + mileageUriSub + "/");
            for (String halfPayUriSub : halfPayUriSubArr) {
                String halfPayMileageUrl = mileageUrl.replaceFirst("/" + mileageUriSub + "/", "/" + mileageUriSub + halfPayUriSub + "/");
                int pageCount = getPageCount(halfPayMileageUrl);
                for (int i = 1; i <= pageCount; i++) {
                    try {
                        String halfPayMileagePageUrl = halfPayMileageUrl.replaceFirst("<page>", "" + i);
                        Document doc = getDoc(halfPayMileagePageUrl);
                        if (null == doc) {
                            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity doc: " + halfPayMileagePageUrl);
                            continue;
                        }
                        Elements listConElements = doc.select(".list-con");
                        if (null == listConElements || listConElements.size() < 1) {
                            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity listConElements: " + halfPayMileagePageUrl);
                            continue;
                        }
                        Elements infoElements = listConElements.get(0).select(".con");
                        if (null == infoElements || infoElements.size() < 1) {
                            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByCity infoElements: " + halfPayMileagePageUrl);
                            continue;
                        }
                        for (Element infoElement : infoElements) {
                            try {
                                Element infoPadElement = infoElement.select(".pad").get(0);
                                Element titleElement = infoPadElement.select(".tit").get(0);
                                String infoHref = titleElement.attr("href");
                                String infoName = titleElement.text();
                                String infoId = titleElement.attr("data-carid");
                                String infoCity = infoHref.replaceFirst("/(\\w+)/(\\w+).html", "$1");
                                String infoCityName = CityUtil.getName(SpiderEnum.youxin, infoCity);
                                if (!cityName.equals(infoCityName)) {
                                    continue;
                                }
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
                                if (!idSet.contains(infoId)) {
                                    if ("v5".equals(halfPayUriSub)) {
                                        // 付一半
                                        logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase() + "_h", JSON.toJSONString(vo));
                                    } else {
                                        infoList.add(vo);
                                        logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase(), JSON.toJSONString(vo));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                logToFile("error", e.toString());
                            }
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

    @Override
    public List<CarVo> listByShopId(String shopId) {
        List<CarVo> infoList = new ArrayList<CarVo>();
        String shopUrl = URL_SHOP_TEMPLATE.replaceFirst("<shopId>", shopId);
        int pageCount = getPageCount(shopUrl);
        String city = "";
        String regDateAndMileageRegex = "上牌(.*?)｜里程(.*?)万公里";
        for (int i = 1; i <= pageCount; i++) {
            try {
                String shopPageUrl = shopUrl + "?page=" + i;
                Document doc = getDoc(shopPageUrl);
                if (null == doc) {
                    SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByShopId doc: " + shopPageUrl);
                    continue;
                }
                Elements infoElements = doc.select(".con-car .con");
                if (null == infoElements || infoElements.size() < 1) {
                    SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "listByShopId: " + shopPageUrl);
                }
                if (StringUtils.isBlank(city)) {
                    city = doc.select(".shop-nav .fr .shop-tab .tab-key").get(0).text();
                }
                for (Element infoElement : infoElements) {
                    try {
                        Element infoPadElement = infoElement.select(".pad").get(0);
                        String infoHref = infoElement.select("a").get(1).attr("href");
                        String infoName = infoPadElement.select("h2").text();
                        String infoId = infoHref.replaceFirst("/[^/]+/([^/]*?)\\..*", "$1");
                        String infoCity = city;
                        String regDateAndMileageStr = infoPadElement.select("span").get(0).text();
                        String infoRegDate = regDateAndMileageStr.replaceFirst(regDateAndMileageRegex, "$1").replaceFirst("/", "");
                        String infoMileage = regDateAndMileageStr.replaceFirst(regDateAndMileageRegex, "$2");
                        String infoPriceStr = infoPadElement.select("em").get(0).text();
                        String infoPrice = infoPriceStr.replaceFirst("(.*?)万.*", "$1").replaceAll("¥", "");

                        Map<String, String> paramMap = new HashMap<String, String>();
                        Elements halfPayElements = infoElement.select(".ico-hcar");
                        boolean isHalfPay = false;
                        if (null != halfPayElements && halfPayElements.size() > 0) {
                            isHalfPay = true;
                            paramMap.put("付一半", "是");
                        } else {
                            paramMap.put("付一半", "否");
                        }
                        Elements soldElements = infoElement.select(".collect-ico");
                        boolean isSold = false;
                        if (null != soldElements && soldElements.size() > 0) {
                            isSold = true;
                            paramMap.put("销售状态", "已售");
                        } else {
                            paramMap.put("销售状态", "在售");
                        }

                        CarVo vo = new CarVo();
                        vo.setSrc(SpiderEnum.youxin);
                        vo.setCity(infoCity);
                        vo.setSrcId(infoId);
                        vo.setName(infoName);
                        vo.setRegDate(infoRegDate);
                        vo.setMileage(infoMileage);
                        vo.setPrice(infoPrice);
                        vo.setAddress(infoHref);
                        vo.setShopId(shopId);
                        vo.setParams(paramMap);
                        infoList.add(vo);
                        logToFile(dfDate.format(new Date()) + "/" + vo.getSrc().name().toLowerCase() + "_shop", JSON.toJSONString(vo));
                    } catch (Exception e) {
                        e.printStackTrace();
                        logToFile("error", e.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logToFile("error", e.toString());
            }
        }
        return infoList;
    }

    public CarDetailVo getByUrl(CarVo carVo) {
        // 不同页面示例：
        // 1. http://www.xin.com/beijing/che91003836.html
        // 2. http://www.xin.com/beijing/che14594253.html
        CarDetailVo carDetailVo = new CarDetailVo();
        if (null == carVo) {
            return carDetailVo;
        }
        try {
            String detailUrl = URL_BASE + carVo.getAddress();
            Document detailDoc = getDoc(detailUrl);
            Elements breadNavDoc = detailDoc.select(".cd_m_nav a");
            String cityName = breadNavDoc.get(1).text().replaceAll("二手车", "");

            String htmlStr = detailDoc.html();
            // String jsonInfoRegex = "(?i)(?s).*?(\\{[^\\}]+'城市': '([^']+)','车辆ID':'([^']+)', '店铺ID':'([^']+)', '是否半价':'([^']+)'[^\\}]+}).*";
            String jsonInfoRegex = "(?s).*?'店铺ID':'([^']+)'.*";

            // 平台来源 src
            carDetailVo.setSrc(SpiderEnum.youxin.name());

            // 城市 city
            carDetailVo.setCity(carVo.getCity());

            // 帖子ID id
            carDetailVo.setId(carVo.getSrcId().replaceAll("che", ""));

            // 原帖链接 url
            carDetailVo.setUrl(carVo.getAddress());

            // 名称 name
            carDetailVo.setName(carVo.getName());

            // 品牌名称 brandName
            String brandName = breadNavDoc.get(2).text().replace("二手车", "").replaceFirst(cityName, "");
            carDetailVo.setBrandName(brandName);

            // 车系名称 seriesName
            String seriesName = breadNavDoc.get(3).text().replace("二手", "").replaceFirst(cityName, "");   // 车系名称
            carDetailVo.setSeriesName(seriesName);

            // 车型名称 modelName
            String modelName = detailDoc.select(".cd_m_nav").text().replaceFirst(".*>([^>]+)", "$1");  // 车型名称
            carDetailVo.setModelName(modelName);

            // 车辆颜色 color
            String color = detailDoc.select(".cd_m_i_pz dl").get(1).select("dd").get(2).select(".cd_m_i_pz_val .cd_m_innerlink1").get(0).text();
            carDetailVo.setColor(removeWhiteSpace(color));

            // 行驶里程 mileage
            carDetailVo.setMileage(carVo.getMileage());

            // 排量
            String displacement = detailDoc.select(".cd_m_info_desc .cd_m_info_desc_val").get(4).text().replaceFirst("(.*?)/(.*)", "$1");
            carDetailVo.setDisplacement(displacement);

            // 变速箱
            String gearBox = detailDoc.select(".cd_m_info_desc .cd_m_info_desc_val").get(4).text().replaceFirst("(.*?)/(.*)", "$2");
            carDetailVo.setGearBox(gearBox);

            // 价格 price
            carDetailVo.setPrice(carVo.getPrice());

            // 发布时间 postDate
            String postDate = detailDoc.select(".cd_m_info_desc .cd_m_info_desc_val").get(2).text().replaceAll("-", "");
            carDetailVo.setPostDate(postDate);

            // 上牌时间 regDate
            String regDate = detailDoc.select(".cd_m_info_desc .cd_m_info_desc_val").get(1).text().replaceAll("-", "");
            carDetailVo.setRegDate(regDate);

            // 联系人 contact
            // NOT Exist

            // 联系电话 phone
            // NOT Exist

            // 看车地址 address
            String address = detailDoc.select(".cd_m_info_desc .cd_m_info_desc_val").get(5).text();
            carDetailVo.setAddress(address);

            // 图片 images
            Elements imageElements = detailDoc.select(".cd_m_i_imglist img");
            if (null != imageElements && imageElements.size() > 0) {
                List<String> imageUrlList = new ArrayList<String>();
                for (Element imageElement : imageElements) {
                    String imageUrl = imageElement.attr("data-src");
                    imageUrlList.add(imageUrl);
                }
                carDetailVo.setImages(imageUrlList);
            }

            // 身份类型 identity
            // carDetailVo.setIdentity(identity);

            // 商家名称 shopName
            // carDetailVo.setShopName();

            // 商家id shopId
            // String shopId = htmlStr.replaceFirst(jsonInfoRegex, "$1");
            String shopId = detailDoc.select(".store_click").get(0).attr("onclick").replaceFirst(".*?collect/\\d+/(\\d+).*", "$1");
            carDetailVo.setShopId(shopId);

            // 帖子状态 status
            Elements isSoldElements = detailDoc.select(".cd_m_info_cover_ys");
            carDetailVo.setStatus((null != isSoldElements && isSoldElements.size() > 0) ? "已售" : "在售");

            // 帖子标签 tag
            Elements isYouxinAuthElements = detailDoc.select(".cd_m_h_yxrz");
            List<String> tags = new ArrayList<String>();
            if (null != isYouxinAuthElements && isYouxinAuthElements.size() > 0) {
                tags.add("优信认证");
            }
            Elements isHalfPayElements = detailDoc.select(".cd_m_info_cover_fyb");
            if (null != isHalfPayElements && isHalfPayElements.size() > 0) {
                tags.add("付一半");
            }
            if (tags.size() > 0) {
                carDetailVo.setTags(tags);
            }

            Elements guohuElements = detailDoc.select(".cd_m_info_sjgr");
            Map<String, String> paramMap = new HashMap<String, String>();
            if (null != guohuElements && guohuElements.size() > 0) {
                String guohuStr = guohuElements.get(0).text();
                if (guohuStr.contains("不含过户费")) {
                    paramMap.put("过户费", "不含");
                } else {
                    paramMap.put("过户费", "含");
                }
            }
            Elements paramElements = detailDoc.select(".cd_m_i_pz dl dd");
            if (null != paramElements && paramElements.size() > 0) {
                for (Element paramElement : paramElements) {
                    try {
                        String key = paramElement.select(".cd_m_i_pz_tit").get(0).text();
                        String value = paramElement.select(".cd_m_i_pz_val").get(0).text();
                        paramMap.put(key, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            carDetailVo.setParams(paramMap);
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
        } catch (Exception e) {
            SenderUtil.sendMessage(SenderUtil.MessageLevel.ERROR, "getPageCount: " + url);
        }
        return pageCount;
    }
}