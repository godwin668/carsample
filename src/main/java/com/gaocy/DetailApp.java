package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailApp implements Callable {

    public static final ExecutorService es = Executors.newFixedThreadPool(10);

    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static DateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static File baseDir = new File(ConfUtil.getString("init.log.base.url"));

    private String dateStr;
    private SpiderEnum spiderEnum;
    private List<String> cityList;

    public DetailApp(String dateStr, SpiderEnum spiderEnum, List<String> cityArr) {
        this.dateStr = dateStr;
        this.spiderEnum = spiderEnum;
        this.cityList = cityArr;
    }

    public static void main(String[] args) {
        String dateStr = "20170426";
        SpiderEnum[] spiderEnumArr = { SpiderEnum.youxin, SpiderEnum.che168 };
        List<String> cityArr = null;
        for (SpiderEnum spider : spiderEnumArr) {
            DetailApp app = new DetailApp(dateStr, spider, cityArr);
            es.submit(app);
        }
    }

    /**
     * 获取列表页车源信息
     *
     * @param spider 抓取源
     * @param cityList 城市列表，如果为空，则为所有城市
     * @param dateStr 日期
     * @return
     */
    public static List<CarVo> listCarVo(SpiderEnum spider, List<String> cityList, String dateStr) {
        List<CarVo> carVoList = new ArrayList<CarVo>();
        File todayFile = new File(baseDir, dateStr + "/" + spider.name().toLowerCase() + ".txt");
        if (todayFile.isFile()) {
            try {
                List<String> sampleLines = FileUtils.readLines(todayFile, "UTF-8");
                for (int lineIndex = sampleLines.size() - 1; lineIndex >= 0; lineIndex--) {
                    String sampleLine = sampleLines.get(lineIndex);
                    CarVo carVo = JSON.parseObject(sampleLine, CarVo.class);
                    String srcId = carVo.getSrcId();
                    String carCity = carVo.getCity();
                    if (null == cityList || cityList.size() < 1 || cityList.contains(carCity)) {
                        carVoList.add(carVo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return carVoList;
    }

    public static Set<String> getIds(List<CarVo> carVoList) {
        Set<String> idSet = new HashSet<String>();
        if (null == carVoList || carVoList.size() < 1) {
            return idSet;
        }
        for (CarVo carVo : carVoList) {
            String srcId = carVo.getSrcId();
            idSet.add(srcId);
        }
        return idSet;
    }

    public static Map<String, CarVo> getId2VoMap(List<CarVo> carVoList) {
        Map<String, CarVo> id2VoMap = new HashMap<String, CarVo>();
        if (null == carVoList || carVoList.size() < 1) {
            return id2VoMap;
        }
        for (CarVo carVo : carVoList) {
            String srcId = carVo.getSrcId();
            id2VoMap.put(srcId, carVo);
        }
        return id2VoMap;
    }

    @Override
    public Object call() throws Exception {
        Spider spider = SpiderFactory.getSpider(spiderEnum, null);
        String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
        String yestodayDateStr = dfDate.format(DateUtils.addDays(DateUtils.parseDate(dateStr, "yyyyMMdd"), -1));

        List<CarVo> yestodayCarVoList = listCarVo(spiderEnum, cityList, yestodayDateStr);
        List<CarVo> todayCarVoList = listCarVo(spiderEnum, cityList, dateStr);

        Set<String> yestodayIdSet = getIds(yestodayCarVoList);
        Map<String, CarVo> id2VoMap = getId2VoMap(todayCarVoList);
        Set<String> todayIdSet = id2VoMap.keySet();
        Collection<String> newIdSet = CollectionUtils.subtract(todayIdSet, yestodayIdSet);

        System.out.println(JSON.toJSONString(cityList) + "_" + newIdSet.size());
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + ", info size(yestoday|today|new): (" + yestodayIdSet.size() + "|" + todayIdSet.size() + "|" + newIdSet.size() + ")");
        for (String srcId : newIdSet) {
            CarVo carVo = id2VoMap.get(srcId);
            CarDetailVo carDetailVo = spider.getByUrl(carVo);
            if (null != carDetailVo && StringUtils.isNoneBlank(carDetailVo.getId())) {
                SpiderBase.logToFile(dateStr + "/" + spiderName + "_detail", JSON.toJSONString(carDetailVo));
            }
        }
        SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + ", info size(yestoday|today|new): (" + yestodayIdSet.size() + "|" + todayIdSet.size() + "|" + newIdSet.size() + ")");
        return null;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public SpiderEnum getSpiderEnum() {
        return spiderEnum;
    }

    public void setSpiderEnum(SpiderEnum spiderEnum) {
        this.spiderEnum = spiderEnum;
    }

    public List<String> getCityList() {
        return cityList;
    }

    public void setCityList(List<String> cityList) {
        this.cityList = cityList;
    }
}