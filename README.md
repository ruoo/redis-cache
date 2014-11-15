redis-cache
===========

给予Redis的Method缓存公用jar

#使用
## 添加Jar依赖
'''xml
    <dependency>
         <groupId>com.whosenet</groupId>
         <artifactId>redis-cache</artifactId>
          <version>1.0</version>
    </dependency>
'''

##Spring配置
'''xml
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
'''
##使用方式
'''java
    @Transactional
       @Cacheable(key="getAdminByName",fieldKey="#name")
       public Admin getByName(String name) {
           return adminDao.getByUsername(name);
       }
       @Transactional
       @CacheEvict(key="getAdminByName",fieldKey="#admin.username")
       public void update(Admin admin){
           adminDao.update(admin);
       }
'''



