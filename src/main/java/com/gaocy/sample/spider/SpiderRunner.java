package com.gaocy.sample.spider;

import com.gaocy.sample.vo.InfoVo;
import com.gaocy.sample.spider.Spider;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by godwin on 2017-03-16.
 */
public class SpiderRunner implements Callable<List<InfoVo>> {

    private Spider spider;

    public SpiderRunner(Spider spider) {
        this.spider = spider;
    }

    @Override
    public List<InfoVo> call() throws Exception {
        return null;
    }
}