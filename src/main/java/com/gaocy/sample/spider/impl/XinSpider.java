package com.gaocy.sample.spider.impl;

import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.vo.InfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-16.
 */
public class XinSpider extends SpiderBase implements Spider {

    public XinSpider(String[] cityArr) {
        super(cityArr);
    }

    @Override
    public List<InfoVo> listByCity(String city) {
        List<InfoVo> infoList = new ArrayList<InfoVo>();
        return infoList;
    }
}