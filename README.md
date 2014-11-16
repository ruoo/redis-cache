[![Build Status](https://travis-ci.org/xetorthio/jedis.png?branch=master)](https://github.com/ruoo/redis-cache)
redis-cache
===========

基于Redis的Method缓存公用jar



#使用
## 添加Jar依赖
    <dependency>
         <groupId>com.whosenet</groupId>
         <artifactId>redis-cache</artifactId>
          <version>1.0</version>
    </dependency>


##Spring配置

    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxTotal" value="50" />
        <property name="maxIdle" value="10" />
        <property name="maxWaitMillis" value="1000" />
        <property name="testOnBorrow" value="true" />
    </bean>

    <bean id="jedisPool" class="redis.clients.jedis.JedisPool">
        <constructor-arg index="0" ref="jedisPoolConfig" />
        <constructor-arg index="1" value="127.0.0.1" />
        <constructor-arg index="2" value="6379" />
    </bean>

##使用方式

* 添加缓存
    - key:缓存集合名
    - fieldKey:缓存key,支持SPEL表达式


       @Transactional
       @Cacheable(key="getAdminByName",fieldKey="#name")
       public Admin getByName(String name) {
           return adminDao.getByUsername(name);
       }



* 清理缓存
    - key:缓存集合名
    - fieldKey:缓存key,支持SPEL表达式
    - allEntries:是否清除该集合下所有key的缓存(默认false)



       @Transactional
       @CacheEvict(key="getAdminByName",fieldKey="#admin.username")
       public void update(Admin admin){
           adminDao.update(admin);
       }




