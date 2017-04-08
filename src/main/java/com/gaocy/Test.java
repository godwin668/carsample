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
            String host = "idocv.com";
            CircularFifoQueue<KeyValue<Long, Integer>> hostAccessQueue = hostAccessQueueMap.get(host);
            if (null == hostAccessQueue) {
                hostAccessQueue = new CircularFifoQueue<KeyValue<Long, Integer>>(3);
                hostAccessQueueMap.put(host, hostAccessQueue);
            }
            add(hostAccessQueue);
            add(hostAccessQueue);
            add(hostAccessQueue);
            add(hostAccessQueue);
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