package com.gaocy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    public static void main(String[] args) {
        int p = -16777216;
        System.out.println(Integer.toBinaryString(p));


        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/Users/Godwin/car/1.jpg"));

            int[][] colorMatrix = convertTo2DWithoutUsingGetRGB(img);
            for (int i = 0; i < colorMatrix.length; i ++) {
                System.out.println();
                for (int j = 0; j < colorMatrix[i].length; j++) {
                    System.out.print(Integer.toBinaryString(colorMatrix[i][j]) + " ");
                }
            }


            /*
            int width = img.getWidth();
            int height = img.getHeight();
            for (int i = 1; i < width; i++) {
                for (int j = 1; j < height; j++) {
                    byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
                    // int rgb = img.getRGB(i, j);
                    System.out.println("(" + i + ", " + j + "), " + JSON.toJSONString(pixels));
                }
            }
            */

        } catch (IOException e) {
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