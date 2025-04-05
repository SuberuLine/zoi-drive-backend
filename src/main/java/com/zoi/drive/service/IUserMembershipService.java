package com.zoi.drive.service;

import com.alipay.api.AlipayApiException;
import com.zoi.drive.entity.Result;
import com.zoi.drive.entity.dto.UserMembership;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zoi.drive.entity.dto.UserPlants;
import com.zoi.drive.entity.vo.response.MembershipInfoVO;
import com.zoi.drive.entity.vo.response.PlantsVO;

import java.util.List;

/**
* <p>
*  服务类
* </p>
*
* @author Yuzoi
* @since 2025-04-04
*/
public interface IUserMembershipService extends IService<UserMembership> {

    // 检查用户会员状态
    Result<MembershipInfoVO> getUserMembership(Integer accountId);

    // 检查用户是否有某项权限
    boolean hasPermission(Integer accountId, String permission);

    // 兑换码升级会员
    boolean upgradeMembershipByCode(Integer accountId, String code);

    // 创建支付订单
    String createPaymentOrder(String orderNo, Integer level) throws AlipayApiException;

    // 处理支付宝回调
    boolean handleAlipayCallback(String orderNo, String tradeNo);

}
