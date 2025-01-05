package com.zoi.drive.service.impl;

import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.Account;
import com.zoi.drive.entity.dto.UserCheckin;
import com.zoi.drive.entity.dto.UserDetail;
import com.zoi.drive.mapper.UserCheckinMapper;
import com.zoi.drive.mapper.UserDetailMapper;
import com.zoi.drive.service.IUserCheckinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2024-09-14
*/
@Service
public class UserCheckinServiceImpl extends ServiceImpl<UserCheckinMapper, UserCheckin> implements IUserCheckinService {

    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Resource
    private UserDetailMapper userDetailMapper;

    @Override
    @Transactional
    public Result<String> dailyCheckin(Account account) {
        UserDetail userDetail = userDetailMapper.selectById(account.getDetails());
        UserCheckin obj = userCheckinMapper.selectById(account.getCheckin());
        long reward;

        Date lastCheckin = obj.getLastCheckin();
        if (lastCheckin == null) {
            obj.setCheckinCount(1);
            obj.setCheckinConsecutive(1);
            obj.setLastCheckin(new Date(System.currentTimeMillis()));
            reward = generateRandomLong(1, 80);
        } else {
            // 重复签到
            if (DateUtils.isSameDay(obj.getLastCheckin(), new Date(System.currentTimeMillis()))) {
                return Result.failure(403,"今日已签到");
            }
            // 连续签到逻辑
            if (wasCheckedInYesterday(obj.getLastCheckin())) {
                obj.setCheckinCount(obj.getCheckinCount() + 1);
                obj.setCheckinConsecutive(obj.getCheckinConsecutive() + 1);
                obj.setLastCheckin(new Date(System.currentTimeMillis()));
                reward = generateRandomLong(10, 100);
            } else {
                // 断签逻辑
                obj.setCheckinCount(obj.getCheckinCount() + 1);
                obj.setCheckinConsecutive(1);
                obj.setLastCheckin(new Date(System.currentTimeMillis()));
                reward = generateRandomLong(1, 80);
            }
        }
        obj.setCheckinReward(obj.getCheckinReward() != 0 ? obj.getCheckinReward() + reward : reward);
        userDetail.setTotalStorage(userDetail.getTotalStorage() != 0 ? userDetail.getTotalStorage() + reward : reward);

        try {
            boolean checkinUpdated = userCheckinMapper.updateById(obj) > 0;
            boolean detailUpdated = userDetailMapper.updateById(userDetail) > 0;
            if (checkinUpdated && detailUpdated) {
                return Result.success((reward / 1024 / 1024) + "M",
                        "签到成功,已连续签到" + obj.getCheckinConsecutive() + "天" +
                                "，奖励空间" + (reward / 1024 / 1024) + "M");
            } else {
                return Result.failure(500, "签到失败");
            }
        } catch (Exception e) {
            // 记录异常日志
            log.error("签到过程中发生异常：", e);
            return Result.failure(500, "签到失败");
        }
    }

    /**
     * 判断是否是昨天签到
     * @param lastCheckin 上次签到时间
     * @return T/F
     */
    private static boolean wasCheckedInYesterday(Date lastCheckin) {

        if (lastCheckin == null) {
            return false;
        }

        Calendar lastDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        lastDay.setTime(lastCheckin);
        today.setTime(new Date());

        return lastDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                lastDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1;
    }

    /**
     * 生成指定范围内的随机 long 值
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     * @return 生成的随机 long 值
     */
    private static long generateRandomLong(int min, int max) {
        return ThreadLocalRandom.current().nextLong(min, max) * 1024 * 1024;
    }
}
