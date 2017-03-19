package com.gaocy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.parse(new File("D:/city_taoche.txt"), "UTF-8");
            Elements cityElements = doc.select("li a");
            for (Element cityElement : cityElements) {
                String cityHref = cityElement.attr("href");
                String city = cityHref.replaceFirst("//(\\w+).taoche.com.*", "$1");
                System.out.println(cityElement.text() + "\t" + city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}