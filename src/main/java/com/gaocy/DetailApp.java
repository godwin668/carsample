package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.service.impl.SpiderServiceImpl;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailApp {

    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static File baseDir = new File(ConfUtil.getString("init.log.base.url"));

    // http://www.che168.com/dealer/125701/20443429.html?pvareaid=100519#pos=6#page=1#rtype=1
    protected static String BASE_URL_CHE168 = "http://www.che168.com";

    public static void main(String[] args) {
        List<CarVo> carVoList = listCarVo(SpiderEnum.che168, "北京", "20170403");
        Spider spider = SpiderFactory.getSpider(SpiderEnum.che168, new String[] {"北京"});
        for (CarVo carVo : carVoList) {
            CarDetailVo carDetailVo = spider.getByUrl(carVo);
            SpiderBase.logToFile("infodetail", JSON.toJSONString(carDetailVo));
        }
    }

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
}