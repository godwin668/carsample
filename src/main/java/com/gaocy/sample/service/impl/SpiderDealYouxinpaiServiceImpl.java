package com.gaocy.sample.service.impl;

import com.gaocy.sample.vo.CarVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godwin on 2017-03-22.
 */

@Service
public class SpiderDealYouxinpaiServiceImpl {

    public static void main(String[] args) {
        runDealSpider();
    }

    public static void runDealSpider() {
        // TODO
    }

    public static List<CarVo> getList(String dateMontStr, String seriesId) {
        List<CarVo> list = new ArrayList<CarVo>();
        return list;
    }
}