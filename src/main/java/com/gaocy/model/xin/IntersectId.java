/*
 * Copyright (c) I Doc View. 北京卓软在线信息技术有限公司. All rights reserved.
 * 项目名称：I Doc View在线文档预览系统
 * 文件名称：IntersectId.java
 * Date：20150101
 * Author: godwin
 */

package com.gaocy.model.xin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by godwin on 2017-03-03.
 */
public class IntersectId {
    public static void main(String[] args) {
        try {
            String yestodayIdAllStr = FileUtils.readFileToString(new File("/Users/Godwin/car/xin/xin_ids_20170315.log"), "UTF-8");
            String todayIdAllStr = FileUtils.readFileToString(new File("/Users/Godwin/car/xin/xin_ids_20170316.log"), "UTF-8");

            String[] yestodayIdAllArr = yestodayIdAllStr.split(",");
            String[] todayIdAllArr = todayIdAllStr.split(",");

            Set<String> yestodayIdAllSet = new HashSet<String>(Arrays.asList(yestodayIdAllArr));
            Set<String> todayIdAllSet = new HashSet<String>(Arrays.asList(todayIdAllArr));

            System.out.println("raw arr size: " + yestodayIdAllArr.length + ", " + todayIdAllArr.length);
            System.out.println("new set size: " + yestodayIdAllSet.size() + ", " + todayIdAllSet.size());

            Collection oldColl = CollectionUtils.subtract(yestodayIdAllSet, todayIdAllSet);
            Collection newColl = CollectionUtils.subtract(todayIdAllSet, yestodayIdAllSet);

            System.out.println("old ids: " + oldColl);
            System.out.println("new ids: " + newColl);

            System.out.println("old ids size: " + oldColl.size());
            System.out.println("new ids size: " + newColl.size());

            System.out.println("same: " + CollectionUtils.intersection(yestodayIdAllSet, todayIdAllSet).size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}