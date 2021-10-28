package org.cczzrs.core.redis;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.log4j.Log4j2;

/**
 * @fileName RedisConfig.java
 * @author CCZZRS
 * @date 2020-10-17 14:33:06
 * @description
 */
@Log4j2
@Configuration
@ConfigurationProperties("redis-config")
@EnableCaching // 开启Springboot缓存
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * # 用户信息存到 redis 的有效时间 24小时 24*60*60 spring.redis.user.timeout=86400
     */
    public static long DEF_TIMEOUT = 86400;
    public static long USER_TIMEOUT = DEF_TIMEOUT;
    public static long ROLE_TIMEOUT = DEF_TIMEOUT;
    
    public static final String USER_ID_KEY_DEF = "USER_ID_KEY";
    public static String USER_ID_KEY = USER_ID_KEY_DEF+"::";
    public static final String USER_ROLES_KEY_DEF = "USER_ROLES_KEY";
    public static String USER_ROLE_ID_KEY = "USER_ROLE_ID_KEY:";

    // @Value("defTimeOut")
    public void setDefTimeOut(long defTimeOut){
        DEF_TIMEOUT = defTimeOut;
    }
    // @Value("userIdKey")
    public void setUserIdKey(String userIdKey){
        USER_ID_KEY = userIdKey;
    }
    // @Value("userRoleIdKey")
    public void setUserRoleIdKey(String userRoleIdKey){
        USER_ROLE_ID_KEY = userRoleIdKey;
    }

    /**
     * 在没有指定缓存Key的情况下，key生成策略 自定义策略生成的key 自定义的缓存key的生成策略 若想使用这个key
     * 只需要讲注解上keyGenerator的值设置为keyGenerator即可</br>
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getDeclaringClass().getName());
                Arrays.stream(params).map(Object::toString).forEach(sb::append);
                return sb.toString();
            }
        };
    }

    /**
     * @fileName RedisConfig.java
     * @author CCZZRS
     * @date 2020-10-17 14:47:57
     * @description 设置序列化
     */
    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /**
     * @fileName RedisConfig.java
     * @author CCZZRS
     * @date 2020-10-17 14:47:57
     * @description 设置序列化
     */
    private RedisSerializer<Object> valueSerializer() {
        // 使用GenericJackson2JsonRedisSerializer
        return new GenericJackson2JsonRedisSerializer();
        // return new GenericFastJsonRedisSerializer();
    }

    /**
     * @fileName RedisConfig.java
     * @author CCZZRS
     * @date 2020-10-17 14:41:47
     * @description 缓存管理器 使用Lettuce，和jedis有很大不同
     */
    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        // 关键点，spring cache的注解使用的序列化都从这来，没有这个配置的话使用的jdk自己的序列化，实际上不影响使用，只是打印出来不适合人眼识别
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))// key序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))// value序列化方式
                .disableCachingNullValues().entryTtl(Duration.ofSeconds(DEF_TIMEOUT));// 默认缓存过期时间 
        // RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
        //         .fromConnectionFactory(lettuceConnectionFactory).cacheDefaults(config).transactionAware();
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory).cacheDefaults(config).transactionAware().build();
    }

    /**
     * @fileName RedisConfig.java
     * @author CCZZRS
     * @date 2020-10-17 14:46:07
     * @description RedisTemplate配置 在单独使用redisTemplate的时候 重新定义序列化方式
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info(" --- redis config init --- ");
        // 配置redisTemplate
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer());// key序列化
        redisTemplate.setValueSerializer(valueSerializer());// value序列化
        // redisTemplate.setHashKeySerializer(keySerializer());// Hash key序列化
        // redisTemplate.setHashValueSerializer(valueSerializer());// Hash value序列化
        // redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}