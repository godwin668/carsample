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
    private String[] cityArr;

    public DetailApp(String dateStr, SpiderEnum spiderEnum, String[] cityArr) {
        this.dateStr = dateStr;
        this.spiderEnum = spiderEnum;
        this.cityArr = cityArr;
    }

    public static void main(String[] args) {
        String dateStr = "20170426";
        SpiderEnum[] spiderEnumArr = { SpiderEnum.che168, SpiderEnum.youxin };
        String[] cityArr = null;
        for (SpiderEnum spider : spiderEnumArr) {
            DetailApp app = new DetailApp(dateStr, spider, cityArr);
            es.submit(app);
        }
    }

    /**
     * 获取列表页车源信息
     *
     * @param spider 抓取源
     * @param city 城市
     * @param dateStr 日期
     * @return
     */
    public static List<CarVo> listCarVo(SpiderEnum spider, String city, String dateStr) {
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
                    if (null != city && city.equals(carCity)) {
                        carVoList.add(carVo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Collections.sort(carVoList, new Comparator<CarVo>() {
            @Override
            public int compare(CarVo o1, CarVo o2) {
                String id1 = o1.getSrcId();
                String id2 = o2.getSrcId();
                return id1.compareTo(id2);  // 递增
            }
        });
        return carVoList;
    }

    @Override
    public Object call() throws Exception {
        if (null == cityArr || cityArr.length < 1) {
            cityArr = CityUtil.getAllCityNameBySpider(spiderEnum).toArray(new String[] { });
        }
        for (String city : cityArr) {
            Spider spider = SpiderFactory.getSpider(spiderEnum, new String[] { city });
            String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
            String yestodayDateStr = dfDate.format(DateUtils.addDays(DateUtils.parseDate(dateStr, "yyyyMMdd"), -1));
            List<CarVo> yestodayCarVoList = listCarVo(spiderEnum, city, yestodayDateStr);
            List<CarVo> carVoList = listCarVo(spiderEnum, city, dateStr);

            // 新增车源
            List<CarVo> carVoNewList = new ArrayList<CarVo>();
            for (CarVo carVo : carVoList) {
                String carVoSrcId = carVo.getSrcId();
                boolean isExist = false;
                for (CarVo yestodayCarVo : yestodayCarVoList) {
                    String yestodayCarVoSrcId = yestodayCarVo.getSrcId();
                    if (null != carVoSrcId && carVoSrcId.equals(yestodayCarVoSrcId)) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    carVoNewList.add(carVo);
                }
            }

            System.out.println(city + "_" + carVoNewList.size());
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + " " + city + ", info size(yestoday|today|new): (" + yestodayCarVoList.size() + "|" + carVoList.size() + "|" + carVoNewList.size() + ")");
            for (CarVo carVo : carVoNewList) {
                CarDetailVo carDetailVo = spider.getByUrl(carVo);
                if (null != carDetailVo && StringUtils.isNoneBlank(carDetailVo.getId())) {
                    SpiderBase.logToFile(dfDate.format(new Date()) + "/detail/" + spiderName + "/" + city, JSON.toJSONString(carDetailVo));
                }
            }
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + " " + city + ", info size(yestoday|today|new): (" + yestodayCarVoList.size() + "|" + carVoList.size() + "|" + carVoNewList.size() + ")");
        }
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

    public String[] getCityArr() {
        return cityArr;
    }

    public void setCityArr(String[] cityArr) {
        this.cityArr = cityArr;
    }
}