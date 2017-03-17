package com.gaocy.sample.spider;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.util.ConfUtil;
import com.gaocy.sample.vo.CityEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.gaocy.sample.util.UserAgentUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Godwin on 11/16/16.
 */
public class SpiderBase {

    static File logFile = new File(ConfUtil.getString("init.log.base.url"));

    private static int sleepInterval = ConfUtil.getInt("spider.sleep.time");

    CityEnum[] cityArr;

    public SpiderBase(String[] cityStrArr) {
        if (null == cityStrArr) {
            return;
        }
        cityArr = new CityEnum[cityStrArr.length];
        for (int i = 0; i < cityStrArr.length; i++) {
            cityArr[i] = CityEnum.valueOf(cityStrArr[i]);
        }
    }

    public CityEnum[] getCityArr() {
        return cityArr;
    }

    public void setCityArr(CityEnum[] cityArr) {
        this.cityArr = cityArr;
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
            logToFile("getdocerr", "url: " + url);
            return null;
        }
    }

    static Document getDocException(String url) throws Exception {
        Thread.sleep(sleepInterval);
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
            File parentFile = logFile.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            File curlogFile = new File(logFile, fileName);
            FileUtils.writeStringToFile(curlogFile, msg + "\n", "UTF-8", true);
            System.out.println("[" + fileName + "] " + msg);
        } catch (IOException e) {
            System.out.println("[LOG TO FILE ERROR] " + msg);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}