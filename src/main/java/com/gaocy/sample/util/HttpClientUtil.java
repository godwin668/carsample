package com.gaocy.sample.util;

import com.gaocy.sample.spider.SpiderBase;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by godwin on 2017-03-24.
 */
public class HttpClientUtil {

    private static List<CloseableHttpClient> httpClientPool = new ArrayList<CloseableHttpClient>();
    private static Map<String, Integer> hostLastIndexMap = new HashMap<String, Integer>();
    private static Map<String, Map<Integer, Long>> hostIndexTimeMap = new HashMap<String, Map<Integer, Long>>();
    private static int sleepInterval = ConfUtil.getInt("spider.sleep.time");

    static {
        // grabzz:pVyAerxF@61.155.147.76:10001
        // grabzz:YoZcisu8@61.155.147.76:10002
        // grabzz:ZYYBPb6B@61.155.147.76:10003
        // grabzz:KI8ZPFZT@61.155.147.76:10004
        // grabzz:Xx2Ls3qK@61.155.147.76:10005
        // grabzz:WdNl2JXe@61.155.147.76:10006
        // grabzz:FE1k7mXS@61.155.147.76:10007
        // grabzz:Cm1bmEjf@61.155.147.76:10008
        httpClientPool.add(getClient("61.155.147.76", 10001, "grabzz", "pVyAerxF"));
        httpClientPool.add(getClient("61.155.147.76", 10002, "grabzz", "YoZcisu8"));
        httpClientPool.add(getClient("61.155.147.76", 10003, "grabzz", "ZYYBPb6B"));
        httpClientPool.add(getClient("61.155.147.76", 10004, "grabzz", "KI8ZPFZT"));
        httpClientPool.add(getClient("61.155.147.76", 10005, "grabzz", "Xx2Ls3qK"));
        httpClientPool.add(getClient("61.155.147.76", 10006, "grabzz", "WdNl2JXe"));
        httpClientPool.add(getClient("61.155.147.76", 10007, "grabzz", "FE1k7mXS"));
        httpClientPool.add(getClient("61.155.147.76", 10008, "grabzz", "Cm1bmEjf"));
    }

    public static void main(String args[]) {
        String url = "http://hk.iuvpn.com:8088/";
        System.out.println(get(url));
        System.out.println(get(url));
        System.out.println(get(url));
        System.out.println(get(url));
    }

    public static List<CloseableHttpClient> getHttpClientPool() {
        return httpClientPool;
    }

    private static int getHttpClientIndex(String url) {
        try {
            String host = new URL(url).getHost();
            Integer lastIndex = hostLastIndexMap.get(host);
            if (null == lastIndex) {
                lastIndex = httpClientPool.size() - 1;
            }
            Integer curIndex = (1 + lastIndex) % httpClientPool.size();
            hostLastIndexMap.put(host, curIndex);
            Map<Integer, Long> indexTimeMap = hostIndexTimeMap.get(host);
            if (null == indexTimeMap) {
                indexTimeMap = new HashMap<Integer, Long>();
                hostIndexTimeMap.put(host, indexTimeMap);
            }
            Long curIndexPreTime = indexTimeMap.get(curIndex);
            Long currentTimeLong = System.currentTimeMillis();
            if (null != curIndexPreTime && (curIndexPreTime > (currentTimeLong - sleepInterval))) {
                Thread.sleep(curIndexPreTime - (currentTimeLong - sleepInterval));
            }
            indexTimeMap.put(curIndex, System.currentTimeMillis());
            return curIndex;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String get(String url) {
        CloseableHttpClient httpClient = httpClientPool.get(getHttpClientIndex(url));
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "text/html");
            httpGet.addHeader("Accept-Charset", "utf-8");
            httpGet.addHeader("Accept-Encoding", "gzip");
            httpGet.addHeader("Accept-Language", "en-US,en");
            httpGet.addHeader("User-Agent", UserAgentUtil.get());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                } else {
                    SpiderBase.logToFile("error", url + ", code: " + statusCode);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // httpclient.close();  HttpClient池中的客户端需要复用，请勿关闭
        }
        return "";
    }

    /**
     * 获取HttpClient
     *
     * @param proxyHost
     * @param proxyPort
     * @param userName
     * @param passwd
     * @return
     */
    public static CloseableHttpClient getClient(String proxyHost, int proxyPort, String userName, String passwd) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(userName, passwd));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(new HttpHost(proxyHost, proxyPort))
                .build();
        return httpclient;
    }
}