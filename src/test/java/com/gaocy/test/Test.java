package com.gaocy.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.gaocy.sample.spider.Spider;
import com.gaocy.sample.spider.impl.Che168Spider;
import com.gaocy.sample.vo.CarDetailVo;
import com.gaocy.sample.vo.CarVo;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    public static void main(String[] args) {
        try {
            String modelStr = FileUtils.readFileToString(new File("/Users/Godwin/car/youxinpai_model2.txt"), "UTF-8");
            modelStr = StringEscapeUtils.unescapeHtml4(modelStr);

            // JSONObject jsonObj = JSON.parseObject(modelStr, Feature.AllowComment, Feature.AllowArbitraryCommas, Feature.AllowSingleQuotes);
            JSONArray jsonArr = JSON.parseArray(modelStr);

            for (Object obj : jsonArr) {
                JSONObject jsonObj = (JSONObject) obj;
                Integer brandId = jsonObj.getInteger("brandId");
                Integer carBrandId = jsonObj.getInteger("carBrandId");
                String brandName = jsonObj.getString("brandName");
                JSONArray seriesJsonArr = jsonObj.getJSONArray("carSerialList");
                System.out.println(brandId + "_" + carBrandId + "_" + brandName + ": " + seriesJsonArr);
                for (Object seriesJson : seriesJsonArr) {
                    JSONObject seriesJsonObj = (JSONObject) seriesJson;
                    Integer carMakeId = seriesJsonObj.getInteger("carMakeId");
                    Integer makeName = seriesJsonObj.getInteger("makeName");
                    Integer seiralId = seriesJsonObj.getInteger("seiralId");
                    Integer carSeiralId = seriesJsonObj.getInteger("carSeiralId");
                    Integer seiralName = seriesJsonObj.getInteger("seiralName");
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                // argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

}