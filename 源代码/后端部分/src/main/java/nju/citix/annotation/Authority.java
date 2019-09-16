package nju.citix.annotation;

import nju.citix.utils.JWTUtil;

import java.lang.annotation.*;

/**
 * 注在Controller类或方法上，表明该类或该方法对应的请求
 * 需要相应的权限
 *
 * @author jiang hui
 * @date 2019/8/19
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authority {
    /**
     * 权限要求，默认为已登录用户才可访问
     * JWTUtil.ALL 所有用户，包括匿名用户，即不需要登录即可
     * JWTUtil.LOGIN 已登录用户，包括消费者和管理员
     * JWTUtil.CUSTOMER 仅消费者可访问
     * JWTUtil.MANAGER 仅管理员可访问
     * JWTUtil.SUPER 仅超级管理员可访问
     *
     * @return 权限要求，默认为已登录用户
     */
    String value() default JWTUtil.LOGIN;
}
