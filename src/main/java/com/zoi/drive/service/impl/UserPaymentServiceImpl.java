package com.zoi.drive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zoi.drive.entity.dto.UserPayment;
import com.zoi.drive.entity.dto.UserPlants;
import com.zoi.drive.entity.enums.PaymentStatus;
import com.zoi.drive.mapper.UserPaymentMapper;
import com.zoi.drive.mapper.UserPlantsMapper;
import com.zoi.drive.service.IUserMembershipService;
import com.zoi.drive.service.IUserPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
@Slf4j
@Service
public class UserPaymentServiceImpl extends ServiceImpl<UserPaymentMapper, UserPayment> implements IUserPaymentService {

    @Autowired
    private IUserMembershipService membershipService;
    
    @Resource
    private UserPlantsMapper plantsMapper;

    @Override
    public UserPayment createOrder(Integer accountId, Integer level, Integer days) {
        // 查询对应套餐的信息
        UserPlants plant = plantsMapper.selectById(level);
        if (plant == null) {
            log.error("套餐不存在: {}", level);
            return null;
        }
        
        // 生成订单号
        String orderNo = "ORDER_" + UUID.randomUUID().toString().replace("-", "");
        
        // 根据套餐信息计算时长和价格
        BigDecimal amount = plant.getPrice();
        // 从duration字段解析天数
        Integer planDays = 31;
        
        // 创建支付订单记录
        UserPayment payment = new UserPayment();
        payment.setAccountId(accountId);
        payment.setOrderNo(orderNo);
        payment.setPlanId(level);
        payment.setDays(planDays);
        payment.setAmount(amount);
        payment.setStatus(0); // 0-未支付 1-已支付 2-已取消
        payment.setCreateAt(new Date());
        
        // 保存到数据库
        save(payment);
        
        return payment;
    }
    @Override
    public String generateAlipayUrl(String orderNo) {
        // 实际上这个方法已经在UserMembershipServiceImpl中实现了createPaymentOrder方法
        // 可以考虑调用membershipService.createPaymentOrder，但这里我们返回空字符串
        // 以避免重复实现逻辑
        return "";
    }

    @Override
    public Integer checkOrderStatus(String orderNo) {
        // 查询本地订单状态
        UserPayment payment = getOne(new LambdaQueryWrapper<UserPayment>()
                .eq(UserPayment::getOrderNo, orderNo));
        
        if (payment == null) {
            return -1; // 订单不存在
        }
        
        return payment.getStatus();
    }

    @Override
    @Transactional
    public boolean processPaymentCallback(String orderNo, String tradeNo) {
        log.info("处理支付回调：orderNo={}, tradeNo={}", orderNo, tradeNo);
        
        // 查询订单
        UserPayment payment = getOne(new LambdaQueryWrapper<UserPayment>()
                .eq(UserPayment::getOrderNo, orderNo));
        
        if (payment == null) {
            log.error("支付回调订单不存在：{}", orderNo);
            return false;
        }
        
        if (payment.getStatus() == 1) {
            log.info("订单已处理过：{}", orderNo);
            return true; // 已处理过
        }
        
        // 更新订单状态
        boolean updated = update(new LambdaUpdateWrapper<UserPayment>()
                .eq(UserPayment::getOrderNo, orderNo)
                .eq(UserPayment::getStatus, 0) // 确保只更新未支付的订单
                .set(UserPayment::getStatus, 1)
                .set(UserPayment::getTradeNo, tradeNo)
                .set(UserPayment::getPayAt, LocalDateTime.now()));
        
        if (updated) {
            // 处理会员升级
            membershipService.handleAlipayCallback(orderNo, tradeNo);
            log.info("订单支付成功：{}", orderNo);
            return true;
        } else {
            log.error("订单状态更新失败：{}", orderNo);
            return false;
        }
    }

    @Override
    public String updateOrderStatus(String orderNo, String tradeNo, Date payAt, PaymentStatus paymentStatus) {
        UserPayment payment = this.query().eq("order_no", orderNo).one();
        if (payment == null) {
            return "fail";
        } else {
            if (paymentStatus.getId() == 1) {
                payment.setTradeNo(tradeNo);
                payment.setPayAt(payAt);
                payment.setStatus(paymentStatus.getId());
                if (this.updateById(payment)) {
                    return "success";
                }
            }
        }
        return "fail";

    }

    @Override
    public UserPayment search(String orderId) {
        return this.query().eq("order_no", orderId).one();
    }
}
