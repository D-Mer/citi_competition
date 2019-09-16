package nju.citix.annotation;

import java.lang.annotation.*;

/**
 * 注在Controller类或方法上，表明该类或该方法对应的请求
 * 需要进行身份认证
 *
 * @author jiang hui
 * @date 2019/8/15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserLoginToken {
}
