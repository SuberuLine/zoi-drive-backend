package com.zoi.drive.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.zoi.drive.entity.Result;
import com.zoi.drive.utils.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description SaToken 全局拦截鉴权配置
 * @Author Yuzoi
 * @Date 2024/9/12 22:24
 **/
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
                    SaRouter.match("/**")
                            .notMatch("/api/auth/**","/swagger-ui/**", "/image/**",
                                    "/api/file/download/**",  "/api/file/preview/**")
                            .check(r -> StpUtil.checkLogin());

                    SaRouter.match("/user/**", r -> StpUtil.checkPermission(Const.DEFAULT_USER_ROLE));
                }))
                .addPathPatterns("/**");
    }

    /**
     * 注册 [Sa-Token 全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**").addExclude("/favicon.ico")

                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    SaManager.getLog().debug("请求path={} 参数={} 提交token={}",
                            SaHolder.getRequest().getRequestPath(),SaHolder.getRequest().getParamMap()
                            , StpUtil.getTokenValue());
                    // ...
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    SaManager.getLog().error("请求处理出错: " + e.getMessage(), e);
                    if (e instanceof NotLoginException) {
                        return Result.failure(401, "未登录或 token 无效: " + e.getMessage());
                    } else if (e instanceof NotPermissionException) {
                        return Result.failure(403, "无权限访问");
                    } else if (e instanceof NotRoleException) {
                        return Result.failure(403, "角色不符合要求");
                    } else if (e instanceof SaTokenException) {
                        return Result.failure(401, "认证错误: " + e.getMessage());
                    } else if (e instanceof HttpRequestMethodNotSupportedException) {
                        return Result.failure(405, "请求方法不支持");
                    }
                    return Result.failure(500, "服务器内部错误");
                })

                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()

                            // ---------- 设置跨域响应头 ----------
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "*")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                    ;

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(v -> System.out.println("--------OPTIONS预检请求，不做处理"))
                            .back();
                })
                ;
    }

}
