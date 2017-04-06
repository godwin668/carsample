package com.gaocy.sample.spider;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.util.CityUtil;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.util.UserAgentUtil;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Godwin on 11/16/16.
 */
public class SpiderBase {

    static File logFile = new File(ConfUtil.getString("init.log.base.url"));

    private static int sleepInterval = ConfUtil.getInt("spider.sleep.time");

    String[] cityNameArr;

    protected static DateFormat dfDateTime = new SimpleDateFormat("yyyyMMddHHmmss");
    protected static DateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
    protected static DateFormat dfTime = new SimpleDateFormat("HHmmss");

    public SpiderBase(String[] cityNameArr) {
        this.cityNameArr = cityNameArr;
    }

    public String[] getCityNameArr() {
        return cityNameArr;
    }

    public void setCityArr(String[] cityNameArr) {
        this.cityNameArr = cityNameArr;
    }

    public CarDetailVo getByUrl(CarVo carVo) {
        return null;
    }

    public static Document getDoc(String url) {
        Document doc = null;
        for (int i = 0; i < 5; i++) {
            try {
                doc = getDocException(url);
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                i++;
            }
        }
        if (null != doc) {
            return doc;
        } else {
            logToFile("error", "getDoc error url: " + url);
            return null;
        }
    }

    static Document getDocException(String url) throws Exception {
        Thread.sleep(sleepInterval);
        logToFile("getdocurl", dfDateTime.format(new Date()) + " " + url);
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgentUtil.get())
                .ignoreContentType(true)
                .timeout(5000)
                .get();
        return doc;
    }

    public static void log(Object msg) {
        try {
            FileUtils.writeStringToFile(logFile, msg.toString() + "\n", true);
            System.out.println(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void printList(String title, List<T> list) {
        if (null == list) {
            return;
        }
        log("==>> PRINT LIST " + title + " (" + list.size() + ") <<== START >>>>>>>>");
        for (T t : list) {
            log(" " + JSON.toJSONString(t));
        }
        log("==>> PRINT LIST " + title + " (" + list.size() + ") <<== END >>>>>>>>");
    }

    public static void logToFile(String fileName, String msg) {
        try {
            fileName = fileName.contains(".") ? fileName : (fileName + ".txt");
            File parentFile = logFile.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            File curlogFile = new File(logFile, fileName);
            FileUtils.writeStringToFile(curlogFile, msg + "\r\n", "UTF-8", true);
            System.out.println("[" + fileName + "] " + msg);
        } catch (IOException e) {
            System.err.println("[LOG TO FILE ERROR] " + msg);
        }
    }

    public static String removeWhiteSpace(String str) {
        if (null == str) {
            return null;
        }
        return str.replace(String.valueOf((char) 160), "").replaceAll("\\s", "");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}