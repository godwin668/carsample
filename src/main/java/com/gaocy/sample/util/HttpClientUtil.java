package com.gaocy.sample.util;

import com.alibaba.fastjson.JSON;
import com.gaocy.sample.spider.SpiderBase;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by godwin on 2017-03-24.
 */
public class HttpClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static List<CloseableHttpClient> httpClientPool = new ArrayList<CloseableHttpClient>();
    private static Map<String, Integer> hostLastIndexMap = new HashMap<String, Integer>();
    private static Map<String, Map<Integer, Long>> hostIndexTimeMap = new HashMap<String, Map<Integer, Long>>();
    private static Map<Integer, Integer> httpClientIndexErrorCountMap = new HashMap<Integer, Integer>();
    private static int sleepInterval = ConfUtil.getInt("spider.sleep.time");
    private static Timer httpClientErrorTimer = new Timer();
    private static DateFormat dfDateTime = new SimpleDateFormat("yyyyMMddHHmmss");

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

        httpClientErrorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (httpClientIndexErrorCountMap.size() <= 1) {
                        return;
                    }
                    logger.debug("[HttpClient Timer] Start check httpClientIndexErrorCountMap(" + JSON.toJSONString(httpClientIndexErrorCountMap) + ")");
                    SpiderBase.logToFile("httpclienttimer", "[" + dfDateTime.format(new Date()) + "] Start check httpClientIndexErrorCountMap(" + JSON.toJSONString(httpClientIndexErrorCountMap) + ")");
                    int curErrorClientSize = 0;
                    for(Iterator<Map.Entry<Integer, Integer>> it = httpClientIndexErrorCountMap.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Integer, Integer> entry = it.next();
                        Integer key = entry.getKey();
                        Integer value = entry.getValue();
                        if (null != key && null != value && value > 0) {
                            String content = get("https://www.baidu.com/", key);
                            if (null != content && content.contains("百度一下")) {
                                logger.info("[HttpClient Back to Normal] HttpClient pool index " + key + " error count " + value + " recovered.");
                                SpiderBase.logToFile("httpclienttimer", "[" + dfDateTime.format(new Date()) + "] [HttpClient NORMAL] client pool index " + key + " error count " + value + " back to OK");
                                it.remove();
                            } else {
                                ++curErrorClientSize;
                            }
                        }
                    }
                    int validClientSize = httpClientPool.size() - curErrorClientSize;
                    if (validClientSize < 4) {
                        SpiderBase.logToFile("httpclienterror", "[HttpClient POOL NOT sufficient] valid client percent: " + validClientSize + "/" + httpClientPool.size() + ", reset all...");
                        httpClientIndexErrorCountMap.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SpiderBase.logToFile("httpclienttimer", "[" + dfDateTime.format(new Date()) + "] Timer run Exception: " + e.getMessage());
                }
            }
        }, 0, 20000);
    }

    public static void main(String args[]) {
        String url = "http://hk.iuvpn.com:8088/";
        System.out.println(get(url));
        System.out.println(get(url));
        try {
            Thread.sleep(5000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<CloseableHttpClient> getHttpClientPool() {
        return httpClientPool;
    }

    private static int getHttpClientIndex(String url) {
        int curIndex = 0;
        try {
            String host = new URL(url).getHost();
            Integer lastIndex = hostLastIndexMap.get(host);
            if (null == lastIndex) {
                lastIndex = httpClientPool.size() - 1;
            }
            curIndex = (1 + lastIndex) % httpClientPool.size();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer errorCount = httpClientIndexErrorCountMap.get(curIndex);
        if (null != errorCount && errorCount > 3) {
            logger.warn("[HttpClient NOT available] client pool index " + curIndex + " error count " + errorCount);
            SpiderBase.logToFile("httpclienterror", "[HttpClient NOT available] client pool index " + curIndex + " error count " + errorCount);
            int errorMapOverValveCount = 0;
            Collection<Integer> errorValues = httpClientIndexErrorCountMap.values();
            for (Integer errValue : errorValues) {
                if (null != errorCount && errorCount > 3) {
                    ++errorMapOverValveCount;
                }
            }
            int validClientSize = httpClientPool.size() - errorMapOverValveCount;
            if (validClientSize < 4) {
                try {
                    SpiderBase.logToFile("httpclienterror", "[HttpClient POOL NOT sufficient] valid client percent: " + validClientSize + "/" + httpClientPool.size() + ", reset all...");
                    httpClientIndexErrorCountMap.clear();
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return getHttpClientIndex(url);
        }
        return curIndex;
    }

    private static void increErrorCount(Integer httpClientPoolIndex) {
        Integer preErrorCount = httpClientIndexErrorCountMap.get(httpClientPoolIndex);
        Integer curErrorCount = (null == preErrorCount) ? 1 : (preErrorCount + 1);
        httpClientIndexErrorCountMap.put(httpClientPoolIndex, curErrorCount);
        logger.warn("[HttpClient ERROR] client pool index " + httpClientPoolIndex + " error count " + curErrorCount + ", cur error count map: " + JSON.toJSONString(httpClientIndexErrorCountMap));
        SpiderBase.logToFile("httpclienterror", "[HttpClient ERROR] client pool index " + httpClientPoolIndex + " error count " + curErrorCount + ", cur error count map: " + JSON.toJSONString(httpClientIndexErrorCountMap));
    }

    public static String get(String url) {
        int httpClientIndex = getHttpClientIndex(url);
        return get(url, httpClientIndex);
    }

    public static String get(String url, int httpClientPoolIndex) {
        CloseableHttpClient httpClient = httpClientPool.get(httpClientPoolIndex);
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", "text/html");
            httpGet.addHeader("Accept-Charset", "utf-8");
            httpGet.addHeader("Accept-Encoding", "gzip");
            httpGet.addHeader("Accept-Language", "en-US,en");
            httpGet.addHeader("User-Agent", UserAgentUtil.get());
            long startTime = System.currentTimeMillis();
            logger.debug("[HttpClient] START get url(" + url + ")");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                long endTime = System.currentTimeMillis();
                logger.debug("[HttpClient] END get url(" + url + ") elapse: " + ((endTime - startTime) / 1000) + " s, result code: " + statusCode);
                if (statusCode >= 200 && statusCode < 500) {
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                } else {
                    SpiderBase.logToFile("httpclienterror", url + ", code: " + statusCode);
                    if (503 == statusCode) {
                        increErrorCount(httpClientPoolIndex);
                    }
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            increErrorCount(httpClientPoolIndex);
            SpiderBase.logToFile("httpclienterror", url + ", message: " + e.getMessage());
        } finally {
            // httpclient.close();  HttpClient池中的客户端需要复用，请勿关闭
        }
        return get(url);
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
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000).build();
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setProxy(new HttpHost(proxyHost, proxyPort))
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpclient;
    }
}