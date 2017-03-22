package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.impl.GuaziSpider;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    private static Map<String, String> brandMap = new HashMap<String, String>();            // 品牌ID-品牌名称
    private static Map<String, String> seriesMap = new HashMap<String, String>();           // 车系ID-车系名称
    private static Map<String, String> seriesId2BrandIdMap = new HashMap<String, String>(); // 车系ID-品牌ID

    public static void main(String[] args) {
        try {
            File output = new File("/Users/Godwin/car/xinmodel/out_series.txt");

            // 品牌
            String brandRaw = FileUtils.readFileToString(new File("/Users/Godwin/car/xinmodel/raw_brand.txt"), "UTF-8");
            Document brandDoc = Jsoup.parse(new File("/Users/Godwin/car/xinmodel/raw_brand.txt"), "UTF-8");
            Elements brandElements = brandDoc.select("option");
            for (Element brandElement : brandElements) {
                String brandId = brandElement.attr("value");
                String brandName = brandElement.text();
                brandMap.put(brandId, brandName);
                // System.out.println(brandElement.attr("value") + "\t" + brandElement.text());
            }

            // 车系
            String seriesRaw = FileUtils.readFileToString(new File("/Users/Godwin/car/xinmodel/raw_series.txt"), "UTF-8");
            JSONObject seriesObj = JSON.parseObject(seriesRaw);
            // System.out.println(seriesObj.toJSONString());
            for (Map.Entry<String, Object> entry : seriesObj.entrySet()) {
                String seriesId = entry.getKey();
                JSONObject seriesValueJsonObj = JSON.parseObject(entry.getValue().toString()); // {"makeid":"28","seriesname":"起亚KX5","xin_enable":"1","brandid":"129","typeid":"6","seriesid":"3244","scid":"3285"}
                String seriesName = seriesValueJsonObj.getString("seriesname");
                String seriesBrandId = seriesValueJsonObj.getString("brandid");

                seriesMap.put(seriesId, seriesName);
                seriesId2BrandIdMap.put(seriesId, seriesBrandId);

                String infoStr = seriesBrandId + "\t" + brandMap.get(seriesBrandId) + "\t" +
                        seriesId + "\t" + seriesMap.get(seriesId);
                System.out.println(infoStr);
                FileUtils.writeStringToFile(output, infoStr + "\n", "UTF-8", true);
            }

            // 车型
            String modelRaw = FileUtils.readFileToString(new File("/Users/Godwin/car/xinmodel/raw_model.txt"), "UTF-8");
            JSONArray modelJsonArr = JSON.parseArray(modelRaw);
            // {"xin_enable":"1","modeid":"90004864","modename":"2017款 1.6 手动 前行版","year":"2017",
            //      "brandid":"92","displacement":"1.6","gearbox":"2","guideprice":"11.99","marketingprice":"10.07","seriesid":"156"}
            for (Object modelJson : modelJsonArr) {
                JSONObject modelJsonObj = JSON.parseObject(modelJson.toString());
                String modelId = modelJsonObj.getString("modeid");
                String modelName = modelJsonObj.getString("modename");
                String year = modelJsonObj.getString("year");
                String brandId = modelJsonObj.getString("brandid");
                String displacement = modelJsonObj.getString("displacement");
                String gearbox = modelJsonObj.getString("gearbox");
                String guidePrice = modelJsonObj.getString("guideprice");
                String marketingPrice = modelJsonObj.getString("marketingprice");
                String seriesId = modelJsonObj.getString("seriesid");
                String infoStr = brandId + "\t" + brandMap.get(brandId) + "\t" +
                        seriesId + "\t" + seriesMap.get(seriesId) + "\t" +
                        modelId + "\t" + modelName + "\t" +
                        year + "\t" + displacement + "\t" + gearbox + "\t" + guidePrice + "\t" + marketingPrice;

                System.out.println(infoStr);
                FileUtils.writeStringToFile(output, infoStr + "\n", "UTF-8", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}