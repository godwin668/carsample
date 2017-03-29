package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static File baseDir = new File(ConfUtil.getString("init.log.base.url"));

    public static void main(String[] args) {
        Map<String, List<String>> priceDiff = getDiffPrice(SpiderEnum.guazi, 7);
        for (Map.Entry<String, List<String>> entry : priceDiff.entrySet()) {
            String srcId = entry.getKey();
            List<String> priceList = entry.getValue();
            if (null != priceList && priceList.size() > 1) {
                SpiderBase.logToFile("diffprice", srcId + " - " + JSON.toJSONString(priceList));
            } else {
                SpiderBase.logToFile("uniqueprice", srcId + " - " + JSON.toJSONString(priceList));
            }
        }
    }

    /**
     *
     * 获取最近N天的样本价格
     *
     * @param spider
     * @param days
     * @return ID, List(yyyyMMdd price)
     */
    public static Map<String, List<String>> getDiffPrice(SpiderEnum spider, int days) {
        String datePriceRegex = "(\\d{8}) (.*)";
        Map<String, List<String>> priceListMap = new HashMap<String, List<String>>();
        for (int i = (-days); i < 0; i++) {
            File spiderSampleFile = getFile(spider, i);
            if (!spiderSampleFile.isFile()) {
                break;
            }
            SpiderBase.logToFile("process", spiderSampleFile.getAbsolutePath());
            try {
                List<String> sampleLines = FileUtils.readLines(spiderSampleFile, "UTF-8");
                for (int lineIndex = sampleLines.size() - 1; lineIndex >= 0; lineIndex--) {
                    String sampleLine = sampleLines.get(lineIndex);
                    CarVo carVo = JSON.parseObject(sampleLine, CarVo.class);
                    String srcId = carVo.getSrcId();
                    String price = carVo.getPrice();
                    List<String> priceList = priceListMap.get(srcId);
                    String lastPrice = "";
                    if (null == priceList) {
                        priceList = new ArrayList<String>();
                        priceListMap.put(srcId, priceList);
                    } else {
                        String lastDatePriceStr = priceList.get(priceList.size() - 1);
                        if (StringUtils.isNotBlank(lastDatePriceStr) && lastDatePriceStr.matches(datePriceRegex)) {
                            lastPrice = lastDatePriceStr.replaceFirst(datePriceRegex, "$2");
                        }
                    }
                    if (null != price && !price.equals(lastPrice)) {
                        String curDatePriceStr = dfDate.format(DateUtils.addDays(new Date(), i)) + " " + price;
                        priceList.add(curDatePriceStr);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return priceListMap;
    }

    public static File getFile(SpiderEnum spider, int addDay) {
        Date dayBeforeDate = DateUtils.addDays(new Date(), addDay);
        return new File(baseDir, dfDate.format(dayBeforeDate) + "/" + spider.name().toLowerCase() + ".txt");
    }
}