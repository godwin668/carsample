package com.gaocy.sample.spider;

import com.gaocy.sample.spider.impl.Che168Spider;
import com.gaocy.sample.spider.impl.RenrencheSpider;
import com.gaocy.sample.spider.impl.YouxinSpider;
import com.gaocy.sample.spider.impl.GuaziSpider;

/**
 * Created by godwin on 2017-03-17.
 */
public class SpiderFactory {

    public static Spider getSpider(SpiderEnum type, String[] cityNameArr) {
        if (SpiderEnum.guazi == type) {
            return new GuaziSpider(cityNameArr);
        } else if (SpiderEnum.youxin == type) {
            return new YouxinSpider(cityNameArr);
        } else if (SpiderEnum.che168 == type) {
            return new Che168Spider(cityNameArr);
        } else if (SpiderEnum.renrenche == type) {
            return new RenrencheSpider(cityNameArr);
        } else {
            return null;
        }
    }
}