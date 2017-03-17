package com.gaocy.sample;

import com.gaocy.sample.util.ConfUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Created by godwin on 2017-03-17.
 */
public class Test {

    public static void main(String[] args) {
        try {
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties().setEncoding("UTF-8")
                                    .setFileName("conf.properties"));
            Configuration config = builder.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}