package com.gaocy.model.yiche;

import com.alibaba.fastjson.JSON;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.nodes.Document;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Godwin on 11/14/16.
 */
public class YicheModelMain extends YicheBase {

    private static ObjectMapper om = new ObjectMapper();

    private static Map<Integer, String> brandId2NameMap = new LinkedHashMap<Integer, String>();
    private static Map<Integer, Map<Integer, String>> brandIdSeriesId2SpellMap = new LinkedHashMap<Integer, Map<Integer, String>>();

    private static ExecutorService es = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        try {
            long startTime = System.currentTimeMillis();
            String yicheHomeUrl = "http://api.car.bitauto.com/CarInfo/MasterBrandToSerialNew.aspx?type=2&pid=0&rt=master&serias=m&key=master_0_2_m";
            Document yicheBrandDoc = getDoc(yicheHomeUrl);
            String brandRawStr = yicheBrandDoc.text();
            brandRawStr = brandRawStr.replaceFirst("[^=]+=(.*)", "$1");
            JsonNode brandAllJsonNode = om.readTree(brandRawStr);
            Iterator<JsonNode> brandJsonNodeIt = brandAllJsonNode.getElements();
            while (brandJsonNodeIt.hasNext()) {
                JsonNode brandJsonNode = brandJsonNodeIt.next();
                String brandId = brandJsonNode.get("id").getValueAsText();
                String brandName = brandJsonNode.get("name").getValueAsText();
                brandId2NameMap.put(Integer.valueOf(brandId), brandName);
            }
            logToFile("main", "-->> 品牌列表 <<--");
            for (Map.Entry<Integer, String> entry : brandId2NameMap.entrySet()) {
                int brandId = entry.getKey();
                String brandNaem = entry.getValue();
                logToFile("main", brandNaem + "(" + brandId + ")");
            }
            System.out.println("[BRAND]" + brandId2NameMap.size() + " - " + JSON.toJSONString(brandId2NameMap));

            for (Map.Entry<Integer, String> entry : brandId2NameMap.entrySet()) {
                Map<Integer, String> seriesId2SpellMap = new LinkedHashMap<Integer, String>();
                Integer brandId = entry.getKey();                                               // 品牌ID
                String brandName = entry.getValue();                                            // 品牌名称
                String yicheBrand2SeriesUrl = "http://api.car.bitauto.com/CarInfo/MasterBrandToSerialNew.aspx?type=2&pid=" + brandId + "&rt=serial&serias=m&key=serial_" + brandId + "_2_m&include=1";
                Document yicheSeriesDoc = getDoc(yicheBrand2SeriesUrl);
                String seriesRawStr = yicheSeriesDoc.text();
                seriesRawStr = seriesRawStr.replaceFirst("[^=]+=(.*)", "$1");
                JsonNode seriesAllJsonNode = om.readTree(seriesRawStr);
                Iterator<JsonNode> seriesJsonNodeIt = seriesAllJsonNode.getElements();
                while (seriesJsonNodeIt.hasNext()) {
                    JsonNode seriesJsonNode = seriesJsonNodeIt.next();
                    String seriesIdStr = seriesJsonNode.get("id").getValueAsText();
                    int seriesId = Integer.valueOf(seriesIdStr);                             // 车系ID
                    String seriesPid = seriesJsonNode.get("pid").getValueAsText();
                    String seriesName = seriesJsonNode.get("name").getValueAsText();
                    String seriesShowName = seriesJsonNode.get("showName").getValueAsText();    // 车系名称
                    String seriesUrlSpell = seriesJsonNode.get("urlSpell").getValueAsText();    // 获取车型用到 http://car.bitauto.com/***
                    seriesId2SpellMap.put(Integer.valueOf(seriesId), seriesUrlSpell);

                    logToFile("main_task", brandId + "\t" + brandName + "\t" + seriesId + "\t" + seriesName + "\t" + seriesUrlSpell);

                    YicheModelTask seriesTask = new YicheModelTask(brandId, brandName, seriesId, seriesName, seriesUrlSpell);
                    es.submit(seriesTask);
                }
                brandIdSeriesId2SpellMap.put(brandId, seriesId2SpellMap);
                System.out.println("[SERIES]" + brandName + "(" + brandId + ") - " + seriesId2SpellMap.size() + " - " + JSON.toJSONString(seriesId2SpellMap));
            }
            es.shutdown();
            es.awaitTermination(5, TimeUnit.HOURS);
            long endTime = System.currentTimeMillis();
            logToFile("main", "--------------------");
            logToFile("main", "done! elapse: " + (endTime - startTime) + "ms.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}