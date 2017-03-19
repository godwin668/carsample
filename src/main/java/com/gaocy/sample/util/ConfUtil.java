package com.gaocy.sample.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

/**
 * Created by godwin on 2017-03-16.
 */
public class ConfUtil {

    public static Configuration config;

    static {
        try {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties().setEncoding("UTF-8")
                                    .setFileName("conf.properties"));
            config = builder.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConf() {
        return config;
    }

    public static Configuration getConfByName(String filename) {
        try {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties().setEncoding("UTF-8")
                                    .setFileName(filename));
            return builder.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(String key) {
        return getConf().getString(key);
    }

    public static int getInt(String key) {
        return getConf().getInt(key);
    }

    public static void main(String[] args) {
        System.out.println(getString("init.src.list"));
        System.out.println(getInt("spider.sleep.time"));
    }
}