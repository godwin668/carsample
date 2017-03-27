package com.gaocy.sample.util;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by godwin on 2017-03-24.
 */
public class JsoupProxyUtil {

    public static void main(String[] args) {
        try {
            final String authUser = "";
            final String authPassword = "";
            Authenticator.setDefault(
                    new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    authUser, authPassword.toCharArray());
                        }
                    }
            );

            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");

            String str = Jsoup.connect("").get().text();
            System.out.println("str: " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}