package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.spider.SpiderFactory;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class DetailChe168App extends DetailBaseApp {

    public static void main(String[] args) {
        String dateStr = "20170415";
        SpiderEnum spiderEnum = SpiderEnum.che168;
        String[] cityArr = new String[] { "北京", "长沙", "重庆", "石家庄", "天津" };
        // String[] cityArr = CityUtil.getAllCityNameBySpider(spiderEnum).toArray(new String[] {});
        for (String city : cityArr) {
            Spider spider = SpiderFactory.getSpider(spiderEnum, new String[] { city });
            String spiderName = spider.getClass().getSimpleName().toLowerCase().replaceAll("spider", "");
            List<CarVo> carVoList = listCarVo(spiderEnum, city, dateStr);
            System.out.println(city + "_" + carVoList.size());
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] Start processing " + spiderName + " " + city + ", info size: " + carVoList.size());
            for (CarVo carVo : carVoList) {
                CarDetailVo carDetailVo = spider.getByUrl(carVo);
                if (null != carDetailVo && StringUtils.isNoneBlank(carDetailVo.getId())) {
                    SpiderBase.logToFile("cardetail/" + spiderName + "/" + city, JSON.toJSONString(carDetailVo));
                }
            }
            SpiderBase.logToFile("logs/" + dfDate.format(new Date()) + "_" + spiderName, "[SHOP CARDETAIL] [" + dfDateTime.format(new Date()) + "] END processing " + spiderName + " " + city + ", info size: " + carVoList.size());
        }
    }
}