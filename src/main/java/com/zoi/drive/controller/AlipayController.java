package com.zoi.drive.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zoi.drive.config.AlipayConfig;
import com.zoi.drive.entity.enums.PaymentStatus;
import com.zoi.drive.service.IUserPaymentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayConfig alipayConfig;

    @Resource
    private IUserPaymentService userPaymentService;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    /**
     * 支付宝异步通知
     */
    @PostMapping("/notify")
    public String asyncNotify(HttpServletRequest request) throws AlipayApiException, ParseException {
        Map<String, String> params = convertRequestToMap(request);

        // 1. 验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayPublicKey,
                "UTF-8",
                "RSA2"
        );

        if (!signVerified) {
            return "failure"; // 验签失败
        }

        // 2. 处理业务逻辑
        // SA [DEBUG]-->: 请求path=/alipay/notify 参数={gmt_create=2025-04-05 20:46:16, charset=UTF-8, gmt_payment=2025-04-05 20:46:37, notify_time=2025-04-05 20:46:39, subject=白金会员 - 月, sign=cssuCBfjO0MX5h4EYulxSfZG6mLXURT0LHm70PCvI+ZblTqR5VRW8k31hC7RrcQloGD3VWkdGX2wNBbryfZYHScahGD0S3iVRxgPbgVFP8Bb/d9v7j/VAe9x9z7AkVZuRY1AznTRWkhFoYF+BDUs9lOAcuQTfB2zCHvnHKGvpz9FdMtbiW+u8mjf1FHhOVNbuyw7jNZtK4CFSl2SuaZbovaitxRQsrPYxnzG5NisNNpUwfJdVy8Zq4jMUB9f54FUlaoF1wf2+RHvYJYVUE4PCl6iv3uYBmJRZAxyFp/jBjp3MBjkOdvszjCkJGv3UFkdnDFeEQv/rxStUZorGmwehg==, buyer_id=2088722064046612, invoice_amount=60.00, version=1.0, notify_id=2025040501222204638046610505489904, fund_bill_list=[{"amount":"60.00","fundChannel":"ALIPAYACCOUNT"}], notify_type=trade_status_sync, out_trade_no=ORDER_287be11a188d4f01a267a2843316152d, total_amount=60.00, trade_status=TRADE_SUCCESS, trade_no=2025040522001446610505448802, auth_app_id=2021000147677172, receipt_amount=60.00, point_amount=0.00, buyer_pay_amount=60.00, app_id=2021000147677172, sign_type=RSA2, seller_id=2088721064118305} 提交token=null
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String payAt = params.get("gmt_payment");

            // 返回success告知支付宝不再通知
            return userPaymentService.updateOrderStatus(orderNo, tradeNo, sdf.parse(payAt), PaymentStatus.SUCCESS);

        }

        return "failure";
    }

    /**
     * 支付宝同步跳转
     */
    @GetMapping("/return")
    public String syncReturn(HttpServletRequest request, Model model) {
        Map<String, String> params = convertRequestToMap(request);
        // SA [DEBUG]-->: 请求path=/alipay/return 参数={charset=UTF-8, out_trade_no=ORDER_287be11a188d4f01a267a2843316152d, method=alipay.trade.page.pay.return, total_amount=60.00, sign=eZ7jVyPns21AvPEWITL59keFiHLqE+V7XGKhD+zwYHyl6TP4diFRX6R2CUb4WXw/ID0AWkpMJoY3MSoFsSiyKUOO2cs208CseUMY9XPmGlhYgC9ErNstx0K/oooJH4WR/ctn3wdhoOQk4BX7CY5c53AmX25XklAnTj5vhOVkDy8T4gzT3gSEQ2lPkg8t+SJyN0O/5tkPH+uyfKNWlP/t3lvY60CAEqxyMLi+T8UltAiMZWX2n72nK+dC73J86i4Y5BGiwa7ojsz2JR38nPR6GxMTut7Dbsy69K2i2wRD1UVVU1lTlluLydC7x82Yud5GS/MU1Y5bH/5Khj2iLVUUmA==, trade_no=2025040522001446610505448802, auth_app_id=2021000147677172, version=1.0, app_id=2021000147677172, sign_type=RSA2, seller_id=2088721064118305, timestamp=2025-04-05 20:46:45} 提交token=null

        // 可选：验证签名（非必须）
        model.addAttribute("orderNo", params.get("out_trade_no"));
        model.addAttribute("amount", params.get("total_amount"));

        return "支付成功"; // 跳转到支付成功页面
    }

    /**
     * 将Request参数转为Map
     */
    private Map<String, String> convertRequestToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, String.join(",", requestParams.get(name)));
        }
        return params;
    }


}
