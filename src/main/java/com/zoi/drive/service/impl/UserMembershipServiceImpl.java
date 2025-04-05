package com.zoi.drive.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserMembership;
import com.zoi.drive.entity.dto.UserPlants;
import com.zoi.drive.entity.dto.UserPayment;
import com.zoi.drive.entity.vo.response.MembershipInfoVO;
import com.zoi.drive.mapper.UserMembershipMapper;
import com.zoi.drive.mapper.UserPlantsMapper;
import com.zoi.drive.mapper.UserPaymentMapper;
import com.zoi.drive.service.IUserMembershipService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
* <p>
*  服务实现类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
@Service
@Slf4j
public class UserMembershipServiceImpl extends ServiceImpl<UserMembershipMapper, UserMembership> implements IUserMembershipService {

    @Autowired
    private AlipayClient alipayClient;

    @Resource
    private UserMembershipMapper userMembershipMapper;

    @Resource
    private UserPlantsMapper userPlantsMapper;

    @Resource
    private UserPaymentMapper userPaymentMapper;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Override
    public Result<MembershipInfoVO> getUserMembership(Integer accountId) {
        List<UserPlants> plants = userPlantsMapper.selectList(null);
        UserMembership user = this.query().eq("account_id", accountId).one();
        MembershipInfoVO membershipInfoVO = new MembershipInfoVO();
        if (user == null) {
            UserMembership newUser = new UserMembership(null, accountId, 0, null, null);
            if (userMembershipMapper.insert(newUser) > 0){
                membershipInfoVO.setId(0);
                membershipInfoVO.setName(plants.get(0).getName());
                membershipInfoVO.setType(plants.get(0).getType());
                return Result.success(membershipInfoVO);
            }

        } else {
            membershipInfoVO.setId(plants.get(user.getPlantsId()).getId());
            membershipInfoVO.setName(plants.get(user.getPlantsId()).getName());
            membershipInfoVO.setType(plants.get(user.getPlantsId()).getType());
            return Result.success(membershipInfoVO);
        }
        return Result.failure(500, "内部错误，请联系管理员");
    }

    @Override
    public boolean hasPermission(Integer accountId, String permission) {
        return false;
    }

    @Override
    public boolean upgradeMembershipByCode(Integer accountId, String code) {
        return false;
    }

    @Override
    public String createPaymentOrder(String orderNo, Integer level) throws AlipayApiException {
        // 查询订单信息
        UserPayment payment = userPaymentMapper.selectOne(new QueryWrapper<UserPayment>()
                .eq("order_no", orderNo));
        
        if (payment == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }
        
        // 查询套餐信息
        UserPlants plant = userPlantsMapper.selectById(level);
        if (plant == null) {
            throw new RuntimeException("套餐不存在: " + level);
        }
        
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);
        
        // 使用AlipayTradePagePayModel来构建请求
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(payment.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        model.setSubject(plant.getName() + " - " + plant.getDuration());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        
        request.setBizModel(model);
        
        // 发起请求
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            return response.getBody(); // 获取支付表单HTML
        } else {
            throw new RuntimeException("支付宝下单失败: " + response.getMsg() + ", " + response.getSubMsg());
        }
    }

    @Override
    @Transactional
    public boolean handleAlipayCallback(String orderNo, String tradeNo) {
        log.info("处理会员更新：orderNo={}, tradeNo={}", orderNo, tradeNo);

        // 查询支付订单
        UserPayment payment = userPaymentMapper.selectOne(new QueryWrapper<UserPayment>()
                .eq("order_no", orderNo)
                .eq("status", 1)); // 已支付的订单

        if (payment == null) {
            log.error("找不到已支付的订单：{}", orderNo);
            return false;
        }

        // 查询用户当前会员信息
        UserMembership membership = this.query().eq("account_id", payment.getAccountId()).one();

        Date now = new Date(); // 改为 Date 类型

        if (membership == null) {
            // 创建新会员记录（计算到期时间）
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, payment.getDays());
            Date endAt = calendar.getTime();

            membership = new UserMembership(null, payment.getAccountId(), payment.getPlanId(), now, endAt);
            return save(membership);
        } else {
            // 计算新的到期时间
            Calendar calendar = Calendar.getInstance();

            // 判断当前是否已过期
            if (membership.getEndAt() == null || membership.getEndAt().before(now)) {
                // 已过期：从当前时间开始计算
                calendar.setTime(now);
            } else {
                // 未过期：从原到期时间开始计算
                calendar.setTime(membership.getEndAt());
            }

            calendar.add(Calendar.DAY_OF_MONTH, payment.getDays());
            Date newEndAt = calendar.getTime();

            // 更新数据
            membership.setPlantsId(payment.getPlanId());
            membership.setEndAt(newEndAt);
            return updateById(membership);
        }
    }
}
