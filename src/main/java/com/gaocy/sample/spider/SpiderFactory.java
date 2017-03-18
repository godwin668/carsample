package com.gaocy.sample.spider;

import com.gaocy.sample.spider.impl.YouxinSpider;
import com.gaocy.sample.spider.impl.GuaziSpider;

/**
 * Created by godwin on 2017-03-17.
 */
public class SpiderFactory {

    public static Spider getSpider(SpiderEnum type, String[] cityArr) {
        if (SpiderEnum.guazi == type) {
            return new GuaziSpider(cityArr);
        } else if (SpiderEnum.youxin == type) {
            return new YouxinSpider(cityArr);
        } else {
            return null;
        }
    }
}