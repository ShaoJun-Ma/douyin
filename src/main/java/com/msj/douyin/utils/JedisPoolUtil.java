package com.msj.douyin.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolUtil {
    static GenericObjectPoolConfig pool;
    static JedisPool jedisPool;
    static Jedis jedis = null;

    //1、初始化jedis连接池
   static {
        pool = new GenericObjectPoolConfig();
        jedisPool = new JedisPool(pool,"49.232.34.190",6379);
    }

    //2、从连接池获取jedis对象
    public static Jedis getJedis(){
        jedis = jedisPool.getResource();
        return jedis;
    }

    //3、归还jedis对象到连接池中
    public static void close(){
        if(jedis != null){
            jedis.close();
        }
    }
}
