package com.lpl.stu.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    Jedis jedis = new Jedis("116.62.123.4");

    /**
     * 设置 key，value 到redis
     * @param key
     * @param value
     * @return 是否成功 boolean
     */
    public boolean set(String key, String value) {
        try {
            jedis.set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 通过key删除
     * @param key
     * @return 是否成功 boolean
     */
    public boolean delete(String key) {
        Boolean result = false;
        try {
            jedis.del(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过key获取value
     * @param key
     * @return
     */
    public String get(String key) {
        return jedis.get(key);
    }
}
