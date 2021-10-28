package org.cczzrs.core.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * @fileName CacheUtil.java
 * @author CCZZRS
 * @date 2021-09-02 10:36:07
 * @description 注解缓存工具类
 *  */
@Component
public class CacheUtil {
    
    /**
     * @fileName MyController.java
     * @author CCZZRS
     * @date 2021-08-31 14:22:29
     * @description 删除用户的数据和角色数据缓存 
     * */
    @CacheEvict(cacheNames = {RedisConfig.USER_ID_KEY_DEF, RedisConfig.USER_ROLES_KEY_DEF}, key = "#root.target.getUserID()")
    public boolean cleanAllByUser() {
        return true;
    }
    @CacheEvict(cacheNames = {RedisConfig.USER_ID_KEY_DEF, RedisConfig.USER_ROLES_KEY_DEF}, key = "#userID")
    public boolean cleanAllByUser(String userID) {
        return true;
    }
}
