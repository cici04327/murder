package com.murder.common.constant;

/**
 * Redis常量
 */
public class RedisConstant {

    /**
     * 门店缓存key前缀
     */
    public static final String STORE_CACHE_PREFIX = "store:";
    
    /**
     * 用户缓存key前缀
     */
    public static final String USER_CACHE_PREFIX = "user:";
    
    /**
     * 剧本缓存key前缀
     */
    public static final String SCRIPT_CACHE_PREFIX = "script:";
    
    /**
     * 预约缓存key前缀
     */
    public static final String RESERVATION_CACHE_PREFIX = "reservation:";
    
    /**
     * 限流key前缀
     */
    public static final String RATE_LIMIT_PREFIX = "rate_limit:";
    
    /**
     * 缓存过期时间（秒）
     */
    public static final long CACHE_EXPIRE_TIME = 1800; // 30分钟
    
    /**
     * 热点数据缓存过期时间（秒）
     */
    public static final long HOT_DATA_EXPIRE_TIME = 3600; // 1小时
}
