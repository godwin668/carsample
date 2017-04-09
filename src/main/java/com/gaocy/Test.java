package com.gaocy;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by godwin on 2017/3/19.
 */
public class Test {

    private static Map<String, CircularFifoQueue<KeyValue<Long, Integer>>> hostAccessQueueMap = new HashMap<String, CircularFifoQueue<KeyValue<Long, Integer>>>();

    public static void main(String[] args) {
        try {
            String s = "当前位置： 首页> 天津二手车> 天津本田二手车> 天津二手本田思域>本田 思域 2006款 1.8L 手动 VTi豪华版(国Ⅲ)";
            System.out.println(s.replaceFirst(".*>([^>]+)", "$1"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void add(CircularFifoQueue<KeyValue<Long, Integer>> hostAccessQueue) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 100));
            hostAccessQueue.offer(new DefaultKeyValue<Long, Integer>(System.currentTimeMillis(), ThreadLocalRandom.current().nextInt(1, 10)));
            System.out.println(JSON.toJSONString(hostAccessQueueMap));
            System.out.println("last: " + JSON.toJSONString(hostAccessQueue.peek()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}