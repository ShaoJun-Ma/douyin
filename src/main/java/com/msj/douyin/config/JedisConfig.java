package com.msj.douyin.config;

import com.msj.douyin.utils.JedisPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    @Bean
    public Jedis jedis(){
        Jedis jedis = JedisPoolUtil.getJedis();
        return jedis;
    }
}
