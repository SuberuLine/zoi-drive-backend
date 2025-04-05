package com.zoi.drive.service;

import com.zoi.drive.entity.dto.UserPayment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.enums.PaymentStatus;

import java.util.Date;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
public interface IUserPaymentService extends IService<UserPayment> {
    // 创建支付订单
    UserPayment createOrder(Integer accountId, Integer level, Integer days);

    // 生成支付链接
    String generateAlipayUrl(String orderNo);

    // 查询订单状态
    Integer checkOrderStatus(String orderNo);

    // 处理支付回调
    boolean processPaymentCallback(String orderNo, String tradeNo);

    String updateOrderStatus(String orderNo, String tradeNo, Date payAt, PaymentStatus paymentStatus);

    UserPayment search(String orderId);
}
