package com.zoi.drive.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Description 配合Redis 实现限流的工具类
 * @Author Yuzoi
 * @Date 2024/9/15 18:03
 **/
@Component
public class FlowLimitUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 检查是否被限流后再进行限流
     * @param key 查询的键
     * @param blockTime 将该键限流的时间
     * @param unit 时间单位，使用 TimeUnit 枚举
     * @return 检测时如果已经被限流，返回false； 如未被限流，则进行限流操作后返回true
     */
    public boolean checkBeforeLimit(String key, int blockTime, TimeUnit unit) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return false;
        } else {
            stringRedisTemplate.opsForValue().set(key, "1", blockTime, unit);
        }
        return true;
    }

    /**
     * 检查是否被限流后再进行限流
     * @param keys 查询的键数组
     * @param blockTime 将该键限流的时间
     * @param unit 时间单位，使用 TimeUnit 枚举
     * @return 检测时如果已经被限流，返回false； 如未被限流，则进行限流操作后返回true
     */
    public boolean checkBeforeLimit(String[] keys, int blockTime, TimeUnit unit) {
        for (String key : keys) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                return false;
            }
        }
        for (String key : keys) {
            stringRedisTemplate.opsForValue().set(key, "1", blockTime, unit);
        }
        return true;
    }

}
