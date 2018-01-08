package com.qg.global.cache;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 小排骨 on 2018/1/8.
 */
public class OnlineCar {

    private ConcurrentHashMap<String, Channel> cache = new ConcurrentHashMap<>();
    //因为小车没有唯一标识，先用这个代替（正确的方案是小车端会发送一个唯一的标识符过来作为key）
    private AtomicInteger carGenerator = new AtomicInteger(0);


    /**
     * 静态内部类存储单例对象
     */
    private static class OnlineCarHolder {
        private static OnlineCar cache = new OnlineCar();
    }

    /**
     * 获取单例对象
     * @return
     */
    public static OnlineCar getInstance() {
        return OnlineCarHolder.cache;
    }


    public String put(Channel channel) {
        String carId = carGenerator.incrementAndGet()+"";
        cache.put(carId,channel);
        return carId;
    }

    /**
     * 移除小车连接
     * @param carId
     */
    public void remove(String carId) {
        cache.remove(carId);
    }

    /**
     * 获取相应的小车连接
     * @param carId
     * @return
     */
    public Channel get(String carId) {
        return cache.get(carId);
    }

    /**
     * 获取相应的carId
     * @param channel
     * @return
     */
    public String get(Channel channel) {
        for (Map.Entry<String,Channel> entry : cache.entrySet()) {
            if (entry.getValue().equals(channel)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<String> keySet() {
        return new ArrayList<String>(cache.keySet());
    }

}
