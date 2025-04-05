package com.zoi.drive.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.OrderCreateDTO;
import com.zoi.drive.entity.dto.UserPlants;
import com.zoi.drive.entity.dto.UserPayment;
import com.zoi.drive.entity.enums.PaymentStatus;
import com.zoi.drive.entity.vo.response.MembershipInfoVO;
import com.zoi.drive.entity.vo.response.PaymentStatusVO;
import com.zoi.drive.entity.vo.response.PlantsVO;
import com.zoi.drive.service.IUserMembershipService;
import com.zoi.drive.service.IUserPaymentService;
import com.zoi.drive.service.IUserPlantsService;
import com.zoi.drive.service.IUserReedemService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/membership")
@Slf4j
public class MemberShipController {

    @Autowired
    private IUserMembershipService userMembershipService;

    @Autowired
    private IUserReedemService redeemService;

    @Autowired
    private IUserPaymentService paymentService;

    @Resource
    private IUserPlantsService plantsService;

    // 获取套餐信息
    @GetMapping("/plans")
    public Result<List<UserPlants>> getMembershipPlans() {
        return plantsService.getPlants();
    }

    // 获取会员信息
    @GetMapping("/info")
    public Result<MembershipInfoVO> getMembershipInfo(@RequestParam Map<String, String> params) {
        return userMembershipService.getUserMembership(StpUtil.getLoginIdAsInt());
    }

    // 使用兑换码
    @PostMapping("/redeem")
    public Object redeemCode(@RequestParam String code) {
        // 实现逻辑
        return null;
    }

    // 创建支付订单
    @PostMapping("/order/create")
    public Result<String> createOrder(@RequestBody OrderCreateDTO requestDto) throws AlipayApiException {
        if (requestDto.getLevel() == null) {
            return Result.failure(400, "level参数不能为空");
        }
        
        // 获取当前登录用户ID
        Integer accountId = StpUtil.getLoginIdAsInt();
        
        // 获取套餐信息
        UserPlants plant = plantsService.getById(requestDto.getLevel());
        if (plant == null) {
            return Result.failure(400, "套餐不存在");
        }
        
        // 创建订单
        UserPayment payment = paymentService.createOrder(accountId, requestDto.getLevel(), null);
        if (payment == null) {
            return Result.failure(500, "创建订单失败");
        }
        
        // 生成支付链接
        String payForm = userMembershipService.createPaymentOrder(payment.getOrderNo(), requestDto.getLevel());
        
        return Result.success(payForm);
    }

    @GetMapping("/order/status/{orderId}")
    public Result<PaymentStatusVO> checkPaymentStatus(@PathVariable String orderId) {
        // 查询订单状态
        UserPayment payment = paymentService.search(orderId);

        if (payment == null) {
            return Result.failure(400, "订单不存在");
        }

        PaymentStatusVO vo = new PaymentStatusVO();
        BeanUtils.copyProperties(payment, vo);


        return Result.success(vo);
    }
}