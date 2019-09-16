package nju.citix.interceptor;

import nju.citix.annotation.Authority;
import nju.citix.annotation.UserLoginToken;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.manager.ManagerService;
import nju.citix.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * 基于JWT的身份认证拦截器
 *
 * @author jiang hui
 * @date 2019/8/15
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    ManagerService managerService;
    @Autowired
    CustomerService customerService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        if (!(object instanceof HandlerMethod)) return true;//如果不是映射到方法则跳过，如访问静态资源
        HandlerMethod handlerMethod = (HandlerMethod)object;
        Method method = handlerMethod.getMethod();

        //检查是否有@UserLoginToken注解，有则进行认证
        if (method.isAnnotationPresent(UserLoginToken.class) || handlerMethod.getBeanType().isAnnotationPresent(UserLoginToken.class)) {
            // 获取权限级别
            Authority authority = null;
            if (method.isAnnotationPresent(Authority.class)) {
                authority = method.getAnnotation(Authority.class);
            }
            // 获取token
            String token = httpServletRequest.getHeader("token");
            // 执行安全认证
            JWTUtil.securityCheck(token, authority == null ? JWTUtil.LOGIN : authority.value());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
