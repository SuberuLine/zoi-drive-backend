package com.zoi.drive.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoi.drive.annotation.SaTempTokenCheck;
import com.zoi.drive.entity.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 临时token校验切面
 */
@Slf4j
@Aspect
@Component
public class SaTempTokenAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 环绕通知处理被@SaTempTokenCheck注解标记的方法
     */
    @Around("@annotation(com.zoi.drive.annotation.SaTempTokenCheck)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Result.failure(400, "无法获取请求信息");
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SaTempTokenCheck annotation = method.getAnnotation(SaTempTokenCheck.class);
        
        // 获取参数名
        String paramName = annotation.paramName();
        
        // 首先从URL参数中获取token
        String token = request.getParameter(paramName);
        
        // 如果URL参数中没有token，尝试从请求体中获取
        if (token == null || token.isEmpty()) {
            token = getTokenFromRequestBody(request, paramName);
        }
        
        // 检查token是否存在
        if (token == null || token.isEmpty()) {
            return Result.failure(400, "缺少临时token参数");
        }
        
        try {
            // 解析token
            String value = SaTempUtil.parseToken(token, String.class);
            // 如果能成功解析token，继续执行原方法
            if (value == null || value.isEmpty()) {
                throw new Exception("无携带token");
            }

            boolean res = value.equals(StpUtil.getLoginIdAsString() + ":2FA");

            if (res){
                return joinPoint.proceed();
            } else {
                throw new Exception("错误的临时token");
            }
        } catch (Exception e) {
            // 如果解析失败，返回错误信息
            log.error("Token验证失败", e);
            return Result.failure(400, annotation.errorMessage());
        }
    }
    
    /**
     * 从请求体中获取token
     */
    private String getTokenFromRequestBody(HttpServletRequest request, String paramName) {
        // 只处理POST、PUT等可能有请求体的请求
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            return null;
        }
        
        // 检查Content-Type是否为application/json
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
            return null;
        }
        
        try {
            // 读取请求体内容
            String requestBody = getRequestBody(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                // 解析JSON
                Map<String, Object> jsonMap = objectMapper.readValue(requestBody, Map.class);
                // 获取token字段
                Object tokenObj = jsonMap.get(paramName);
                if (tokenObj != null) {
                    return tokenObj.toString();
                }
            }
        } catch (Exception e) {
            log.warn("从请求体中读取token失败", e);
        }
        
        return null;
    }
    
    /**
     * 读取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            // 如果请求已经被包装为ContentCachingRequestWrapper，直接读取缓存内容
            if (request instanceof ContentCachingRequestWrapper wrapper) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                }
            } else {
                // 否则直接读取输入流
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            }
        } catch (IOException e) {
            log.warn("读取请求体失败", e);
        }
        return null;
    }
}