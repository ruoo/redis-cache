package com.whosenet.cache.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Component
public class RedisCacheCache {

    @Resource
    JedisPool jedisPool;


    public void put(String key,String field,Object object){
        Jedis jedis =jedisPool.getResource();
        jedis.hset(key,field, JSON.toJSONString(object));
        jedisPool.returnResource(jedis);
    }

    public String get(String key,String field){
        Jedis jedis =jedisPool.getResource();
        String text=jedis.hget(key,field);
        jedisPool.returnResource(jedis);
        return text;
    }

    public <T> T get(String key,String field,Class<T> clazz){
        String text=get(key, field);
        T result=JSON.parseObject(text, clazz);
        return result;
    }

    public void del(String key,String ... field){
        Jedis jedis =jedisPool.getResource();
        jedis.hdel(key,field);
        jedisPool.returnResource(jedis);
    }

    public void delAllEntries(String key){
        Jedis jedis =jedisPool.getResource();
        jedis.del(key);
        jedisPool.returnResource(jedis);
    }

}
