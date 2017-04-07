package com.gaocy;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.spider.SpiderEnum;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    public static void main(String[] args) {
        String s = "<span class=\"name\">商家</span><i class=\"separated\">|</i>北京顺腾兴业二手车<i class=\"iconfont drop-down\">&#xe916;</i>";
        System.out.println(s);
        // s = s.replaceFirst("(?s)(?i).*?|</i>([^<]+)<i.*", "$1");
        s = s.replaceFirst("(?s)(?i).*?\\|</i>([^<]+).*", "$1");
        System.out.println(s);

    }
}