package com.gaocy.sample.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaocy.sample.model.YouxinpaiModel;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.vo.CarVo;
import com.gaocy.sample.vo.ModelVo;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SpiderDealYouxinpaiServiceImpl {

    private static String URL = "http://www.youxinpai.com/transaction/tradMarket";

    private static DateFormat dfMonth = new SimpleDateFormat("yyyyMM");
    private static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Map<String, ModelVo> seriesMap = YouxinpaiModel.getSeriesMap();
    private static Map<Integer, String> cityMap = new LinkedHashMap<Integer, String>();

    static {
        cityMap.put(201, "北京");
        cityMap.put(2401, "上海");
        cityMap.put(2501, "成都");
        cityMap.put(2601, "天津");
        cityMap.put(501, "广州");
        cityMap.put(3001, "杭州");
        cityMap.put(502, "深圳");
        cityMap.put(2301, "西安");
    }

    public static void main(String[] args) {
        runDealSpider();
    }

    public static void runDealSpider() {
        for (int year = 2017; year > 1976; year--) {
            for (Map.Entry<Integer, String> cityEntry : cityMap.entrySet()) {
                Integer cityId = cityEntry.getKey();
                String cityName = cityEntry.getValue();
                for (Map.Entry<String, ModelVo> seriesEntry : seriesMap.entrySet()) {
                    String seriesId = seriesEntry.getKey();
                    ModelVo seriesVo = seriesEntry.getValue();
                    List<CarVo> carVoList = getList(seriesVo, cityId, year);
                    SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_youxinpai_deal", "[DETAIL] [" + dfDateTime.format(new Date()) + "] query:" + year + "_" + cityName + "_" + seriesId + ", vo size:" + carVoList.size());
                    for (CarVo carVo : carVoList) {
                        SpiderBase.logToFile(dfDate.format(new Date()) + "/youxinpai_deal", JSON.toJSONString(carVo));
                    }
                }
            }
        }
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_youxinpai_deal", "[ALL DONE] [" + dfDateTime.format(new Date()) + "] All DONE.");
    }

    public static List<CarVo> getList(ModelVo model, Integer cityId, Integer year) {
        List<CarVo> list = new ArrayList<CarVo>();

        String brandId = model.getBrandId();
        String seriesId = model.getSeriesId();

        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headerMap.put("Cookie", "ACCESSTOKEN=c4c4d8a755788bc1342cf08621fecfbef034aa408217e413dceef56dfb3574b482517e13fbb5480f2928957c5ce9801edd263109a319120383b8612b4fea40d3e23b682ead4d8e3f494b9a297100a10a4606634073138ff411d8527c79cbdc62234f75afad8518ec3660bf027f11afcebee06c1d1dacfb86cbf0024ed60073518225daf4e9a9ece321a27be89f5c457a7d07647617acffc21a99178daef8cdc84e5fd30d1333c5be73b5a02fd0824be453bade0d0a927d22b56a6571ae68206b41ceea1a641933c4ee39052bab4d3911a489f63e840390166576e0d9cc511b9956dcbaa74d3e56a45d39d9e422420468f0a5250c8bb2de1e4be12bddd53470adcb64b4da7b49734e90b6aa2ecad8f288f6a96421f56a5f9cc7dfdf62f2d2c41396a9e14da7763e03f5db235b905a020b882a4b18600e57a733bae1d8788130398dfa8e42d6178a799830147199bb80a76563b1a3cbe046e88c6f8f4a48ed12be661d90167abfdb1257a9eeec805fd18a6c60f45707fcc3f8e72c0a84754fa394da91bdd3b26614d96253a44141f0b5b2ad99e30a4774eea828cfac5b0c842f31a3d4366e9a7444bc9690dfbb544d7238f9d0955915db0be59cf5d3c359290a887a3eeae8abf9d9f4c3683690aff6ba1566b4f943adfce17e0eb2cee3741856c2e56b35b1bc7f9279ce8c4c9c768a45221a6d4f9e4ef8c7078a6a5073b961e3c2234f19f412674a4dccdf03e2186edda5cbfef099e6436027002d62decdc52b2f2dcf00c1974a52c38a47bb46a787ec72f1ca535ff9596299e10742bfccecce2451371f2b85bef9b06b739892dd2988b9efc01a824a2f1260b9a7352bd47d0f3c10f1a4e353e85b37ebb048485f4a2428f1f069ee5088e37aa9bfca18f8362c0bee7cbdf42ab5d5f69f35b8eed3779c67ae1f58ac7fe95406734507effe5470e0e551313b0f8fee3e5df91ab8cdb6493958bad96f15317e60ca0297a8a88bce373b2467d0b1e8a867eeacb06a18f3078256e24c78774f9de5fc008c93ee39320b6f5c1cf85fb756f061a8f71e936e177b99dd3a97078be8cc4350c3b931db3aa3a66ef626bc5e31ab9f9ea801adc5175f795fc9d95c582bce2d877ae47c264c318d28da8698538b3ccfb3f9132390f16f6b94e4f6c090de6f11011cbca36521ef335854f868a2ef81d1985352bd98535ccbb2c74771242bde0996efa7a4407015b8f0f33e373606e76714fde5314e51a248f727cddfcc10cd7ea5b9172e8f358ebe4469e8da650096cced5f94ab68229562726f3d41e7c3622dd820a33369f67702829da24f494a24362a2c2b9d4911d5be8fa232213cda4471cb16c1383efd83e3676c4b3ffbb532cd9c87c258ef36619de5e29af139bb3189af31679fdde75fe1de91cd065c6c91c0865c023186087de5216acae5b90f0aa60aa8118a0114382a707d9442e12501dd1a417f1b3a03c0295cbc9c3995d31cf6f875eb2c5be3e7");

        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("isAll", "false");
        dataMap.put("curPage", "1");
        dataMap.put("pageSize", "5");
        dataMap.put("brandId", brandId);
        dataMap.put("serialId", seriesId);
        dataMap.put("cityId", "" + cityId);
        dataMap.put("firstRegTime", "" + year);
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_youxinpai_deal_url", brandId + "_" + seriesId + "_" + cityId + "_" + year);
        Document document = null;
        try {
            document = SpiderBase.getOrPostDocByJsoup(URL, false, headerMap, dataMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == document){
            return list;
        }
        String docText = document.text();
        JSONObject jsonObject = JSON.parseObject(docText);
        JSONArray dealJsonObjArr = jsonObject.getJSONObject("body").getJSONObject("data").getJSONArray("tradeMarketList");
        for (Object dealJsonObjObj : dealJsonObjArr) {
            JSONObject dealJsonObj = (JSONObject) dealJsonObjObj;
            CarVo carVo = new CarVo();
            String brandName = dealJsonObj.getString("brandName");
            String seriesName = dealJsonObj.getString("seriesName");
            carVo.setName(brandName + " " + seriesName + " " + dealJsonObj.getString("modelName"));
            carVo.setPrice(dealJsonObj.getString("buyerFee"));
            carVo.setRegDate(dealJsonObj.getString("getLicenseDate").replaceAll("-", ""));
            carVo.setMileage(dealJsonObj.getString("mileage"));
            Map<String, String> metaMap = new HashMap<String, String>();
            metaMap.put("color", dealJsonObj.getString("carBodyColor"));
            metaMap.put("carType", dealJsonObj.getString("carType"));
            metaMap.put("cityName", dealJsonObj.getString("cityName"));
            metaMap.put("conditionGrade", dealJsonObj.getString("conditionGrade"));
            metaMap.put("draftTime", dealJsonObj.getString("draftTime"));
            metaMap.put("brandName", dealJsonObj.getString("brandName"));
            metaMap.put("seriesName", dealJsonObj.getString("seriesName"));
            carVo.setParams(metaMap);
            list.add(carVo);
            System.out.println(JSON.toJSONString(carVo));
        }
        return list;
    }
}