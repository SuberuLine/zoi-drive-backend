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
import java.util.Random;

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
        BigDecimal reward;
        // 重复签到
        if (DateUtils.isSameDay(obj.getLastCheckin(), new Date(System.currentTimeMillis()))) {
            return Result.failure(403,"今日已签到");
        }
        // 连续签到逻辑
        if (wasCheckedInYesterday(obj.getLastCheckin())) {
            obj.setCheckinCount(obj.getCheckinCount() + 1);
            obj.setCheckinConsecutive(obj.getCheckinConsecutive() + 1);
            obj.setLastCheckin(new Date(System.currentTimeMillis()));
            reward = generateRandomDecimal(10, 100);
        } else {
            // 断签逻辑
            obj.setCheckinCount(obj.getCheckinCount() + 1);
            obj.setCheckinConsecutive(1);
            obj.setLastCheckin(new Date(System.currentTimeMillis()));
            reward = generateRandomDecimal(1, 80);
        }
        obj.setCheckinReward(obj.getCheckinReward().add(reward));
        userDetail.setTotalStorage(userDetail.getTotalStorage().add(reward));
        return userCheckinMapper.updateById(obj) > 0 && userDetailMapper.updateById(userDetail) > 0 ?
                Result.success(reward + "M",
                        "签到成功,已连续签到"+obj.getCheckinConsecutive()+"天"+"，奖励空间" + reward + "M") :
                Result.failure(500,"签到失败");
    }

    /**
     * 判断是否是昨天签到
     * @param lastCheckin 上次签到时间
     * @return T/F
     */
    private static boolean wasCheckedInYesterday(Date lastCheckin) {
        Calendar lastDay = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        lastDay.setTime(lastCheckin);
        today.setTime(new Date());

        if (lastDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                lastDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
            return true;
        } else return false;
    }

    /**
     * 生成指定范围内的BigDecimal类型的随机数，并保留两位小数。
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     * @return 生成的BigDecimal类型的随机数
     */
    private static BigDecimal generateRandomDecimal(int min, int max) {
        Random random = new Random();
        int intValue = random.nextInt(max - min) + min;
        int fractionValue = random.nextInt(100);

        BigDecimal decimalValue = new BigDecimal(intValue + "." + fractionValue);
        return decimalValue.setScale(2, RoundingMode.HALF_UP);
    }
}
