package com.gaocy.sample.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaocy.sample.vo.ModelVo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by godwin on 2017-06-06.
 */
public class YouxinpaiModel {

    public static Map<String, ModelVo> seriesMap = new HashMap<String, ModelVo>();

    static {
        loadSeries();
    }

    private static void loadSeries() {
        ClassLoader classLoader = YouxinpaiModel.class.getClassLoader();
        File file = new File(classLoader.getResource("model/youxinpai_series.txt").getFile());
        try {
            String modelStr = FileUtils.readFileToString(file, "UTF-8");
            JSONArray jsonArr = JSON.parseArray(modelStr);
            for (Object obj : jsonArr) {
                JSONObject jsonObj = (JSONObject) obj;
                Integer brandId = jsonObj.getInteger("brandId");
                Integer carBrandId = jsonObj.getInteger("carBrandId");
                String brandName = jsonObj.getString("brandName");
                JSONArray seriesJsonArr = jsonObj.getJSONArray("carSerialList");
                // System.out.println(brandId + "_" + carBrandId + "_" + brandName + ": " + seriesJsonArr);
                for (Object seriesJson : seriesJsonArr) {
                    JSONObject seriesJsonObj = (JSONObject) seriesJson;
                    Integer carMakeId = seriesJsonObj.getInteger("carMakeId");
                    String makeName = seriesJsonObj.getString("makeName");
                    Integer seriesId = seriesJsonObj.getInteger("seiralId");
                    Integer carSeriesId = seriesJsonObj.getInteger("carSeiralId");
                    String seriesName = seriesJsonObj.getString("seiralName");
                    System.out.println(brandName + "(" + carBrandId + "), " + seriesName + "(" + carSeriesId + ")");

                    ModelVo modelVo = new ModelVo();
                    modelVo.setBrandId("" + carBrandId);
                    modelVo.setBrandName(brandName);
                    modelVo.setSeriesId("" + carSeriesId);
                    modelVo.setSeriesName(seriesName);
                    seriesMap.put("" + carSeriesId, modelVo);
                }
            }

            System.out.println("series size: " + seriesMap.size());

            for (Map.Entry<String, ModelVo> entry : seriesMap.entrySet()) {
                String key = entry.getKey();
                ModelVo modelVo = entry.getValue();
                System.out.println(key + " - " + JSON.toJSONString(modelVo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ModelVo> getSeriesMap() {
        return seriesMap;
    }

    public static ModelVo getSeries(String seriesId) {
        return seriesMap.get(seriesId);
    }

    public static void main(String[] args) {

    }
}