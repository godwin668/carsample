package com.gaocy.model.yiche;

import com.alibaba.fastjson.JSON;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Godwin on 11/15/16.
 */
public class YicheModelTask extends YicheBase implements Callable<List<YicheModelVo>> {

    private static Logger logger = LoggerFactory.getLogger(YicheModelTask.class);

    private int brandId;
    private String brandName;
    private int seriesId;
    private String seriesName;
    private String seriesSpell;

    public YicheModelTask(int brandId, String brandName, int seriesId, String seriesName, String seriesSpell) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.seriesSpell = seriesSpell;
    }

    @Override
    public List<YicheModelVo> call() throws Exception {
        List<YicheModelVo> list = new ArrayList<YicheModelVo>();
        try {
            String series2ModelUrl = "http://car.bitauto.com/" + seriesSpell;                                   // 车系链接
            Document series2ModelDoc = getDoc(series2ModelUrl);
            Elements modelCarListElements = series2ModelDoc.getElementById("car_list").getElementsByTag("tr");

            // 停产车型
            try {
                Elements noSaleYearListElements = series2ModelDoc.getElementById("carlist_nosaleyear").getElementsByTag("li");
                for (Element noSaleYearListElement : noSaleYearListElements) {
                    String noSaleYearValue = noSaleYearListElement.getElementsByTag("a").get(0).attr("id");
                    String series2ModelOldUrl = series2ModelUrl + "/" + noSaleYearValue;
                    Document series2ModelOldDoc = getDoc(series2ModelOldUrl);
                    Elements modelCarListOldElements = series2ModelOldDoc.getElementById("car_list").getElementsByTag("tr");
                    if (null != modelCarListOldElements && modelCarListOldElements.size() > 0) {
                        modelCarListElements.addAll(modelCarListOldElements);
                    }
                }
            } catch (Exception e) {
                logToFile("nosalefail", series2ModelUrl + " - " + e.getMessage());
            }

            Set<Integer> modelIdSet = new HashSet<Integer>();
            for (Element modelCarListElement : modelCarListElements) {                                          // 车型列表
                try {
                    Elements modelCarListElementTds = modelCarListElement.getElementsByTag("td");
                    if (null != modelCarListElementTds && modelCarListElementTds.size() > 4) {
                        String modelHref = modelCarListElementTds.get(0).getElementsByTag("a").get(0).attr("href"); // 车型 href
                        String modelIdStr = modelHref.replaceFirst("/.*?/m(\\d+).*", "$1");
                        Integer modelId = Integer.valueOf(modelIdStr);                                              // 车型 ID
                        if (modelIdSet.contains(modelId)) {
                            System.out.println("[MODEL SET Exist] " + modelId + " - " + JSON.toJSON(modelIdSet));
                            continue;
                        }
                        modelIdSet.add(modelId);
                        String modelName = modelCarListElementTds.get(0).getElementsByTag("a").get(0).text();       // 车型 名称
                        String modelPrice = modelCarListElementTds.get(3).getElementsByTag("span").get(0).text();   // 车型 厂商指导价

                        String modelUrl = series2ModelUrl + "/m" + modelId;
                        Element modelBasicInfoDoc = getDoc(modelUrl).getElementsByClass("zs-m-card").get(0);

                        String modelGuidePrice = modelBasicInfoDoc.getElementById("jiaGeDetail").getElementsByClass("s1").get(0).getElementsByTag("em").get(0).text();
                        String modelGear = modelBasicInfoDoc.getElementsByClass("ul-set").get(0).getElementsByTag("li").get(1).getElementsByTag("span").text();
                        String modelDisplacement = modelBasicInfoDoc.getElementsByClass("ul-set").get(0).getElementsByTag("li").get(2).getElementsByTag("span").text();

                        String modelBaojiaUrl = modelUrl + "/baojia/c201";

                        String modelRefPrice = "";
                        try {
                            modelRefPrice = getDoc(modelBaojiaUrl).getElementsByClass("zpp-card").get(0).getElementsByClass("card-tit").get(0).getElementsByTag("strong").get(0).text();
                        } catch (Exception e) {
                            logToFile("noRefPrice", modelBaojiaUrl);
                            modelRefPrice = "0";
                        }
                        YicheModelVo vo = new YicheModelVo(brandId, brandName, seriesId, seriesName, seriesSpell, modelId, modelName, modelDisplacement, modelGear, modelGuidePrice, modelRefPrice);
                        vo.setModelUrl(modelUrl);
                        log("[ADD Model] " + JSON.toJSONString(vo));
                        list.add(vo);
                    }
                } catch (Exception e) {
                    logger.warn("[MODEL FAIL] ", e);
                    log("[MODEL FAIL] " + e.getMessage());
                }
            }

            printList("[SUCCESS MODEL]", list);

            for (YicheModelVo vo : list) {
                String modelInfoStr = vo.getBrandId() + "\t" + vo.getBrandName() + "\t" + vo.getSeriesId() + "\t" + vo.getSeriesName() + "\t" + vo.getModelId() + "\t" + vo.getModelName() + "\t" + vo.getModelDisplacement() + "\t" + vo.getModelGear() + "\t" + vo.getModelGuidePrice() + "\t" + vo.getModelRefPrice() + "\t" + vo.getModelUrl();
                logToFile("success", modelInfoStr);
            }

            // do something
            return list;
        } catch (Exception e) {
            logger.warn("[SERIES FAIL] ", e);
            log("[MODEL FAIL] " + e.getMessage());
            return list;
        }
    }

    public static void main(String[] args) {
        int brandId = 3;
        String brandName = "宝马";
        int seriesId = 3991;
        String seriesName = "宝马M3";
        String seriesUrlSpell = "baomam3";
        YicheModelTask seriesTask = new YicheModelTask(brandId, brandName, seriesId, seriesName, seriesUrlSpell);
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(seriesTask);
    }
}