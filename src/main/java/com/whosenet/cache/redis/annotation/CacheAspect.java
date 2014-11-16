package com.whosenet.cache.redis.annotation;

import com.whosenet.cache.redis.RedisCacheCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

@Component
@Aspect
public class CacheAspect {

    @Autowired
    RedisCacheCache redisCacheCache;


    /**
     * 缓存.
     * 缓存key=value+"&"+methodName+"&"+argName+"&"+argValue
     * */
    @Around("@annotation(com.whosenet.cache.redis.annotation.Cacheable)")
    public Object cache(ProceedingJoinPoint pjp) {
        Object result=null;

        Method method=getMethod(pjp);

        Cacheable cacheable=method.getAnnotation(com.whosenet.cache.redis.annotation.Cacheable.class); //获取注解对象实例

        String fieldKey =parseKey(cacheable.fieldKey(),method,pjp.getArgs()); //注解Key

        Class returnType=((MethodSignature)pjp.getSignature()).getReturnType(); //方法返回

        result= redisCacheCache.get(cacheable.key(), fieldKey,returnType); //从缓存中查找

        if(result==null){
            try {
                result=pjp.proceed();
                Assert.notNull(fieldKey);
                redisCacheCache.put(cacheable.key(),fieldKey, result);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /** 清楚缓存 */
    @Around(value="@annotation(com.whosenet.cache.redis.annotation.CacheEvict)")
    public Object evict(ProceedingJoinPoint pjp ){
        Method method=getMethod(pjp);
        CacheEvict cacheEvict=method.getAnnotation(com.whosenet.cache.redis.annotation.CacheEvict.class); //获取注解对象实例
        String fieldKey =parseKey(cacheEvict.fieldKey(),method,pjp.getArgs()); //注解Key
        if(cacheEvict.allEntries()){
            redisCacheCache.delAllEntries(cacheEvict.key());
        }else{
            redisCacheCache.del(cacheEvict.key(), fieldKey); //从缓存中删除
        }
        return null;
    }




    /**
     *  获取被拦截方法对象
     *
     *  MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     *	而缓存的注解在实现类的方法上
     *  所以应该使用反射获取当前对象的方法对象
     */
    public Method getMethod(ProceedingJoinPoint pjp){
        //获取参数的类型
        Object [] args=pjp.getArgs();
        Class [] argTypes=new Class[pjp.getArgs().length];
        for(int i=0;i<args.length;i++){
            argTypes[i]=args[i].getClass();
        }
        Method method=null;
        try {
            method=pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(),argTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return method;

    }


    /**
     *	获取缓存的key.
     *	key 定义在注解上，支持SPEL表达式
     */
    private String parseKey(String key,Method method,Object [] args){

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =new LocalVariableTableParameterNameDiscoverer();
        String [] paraNameArr=u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context,String.class);
    }
}