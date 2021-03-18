package cn.coderap.controller.interceptor;

import cn.coderap.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器使用的必要性：
 * 基于权限的拦截：正常情况下，用户登录后修改用户信息，但是如果不拦截判断用户修改的是否是本人信息，那么别人就可以通过
 * 其他的某种手段（我用postman试过）来更改非自己的用户信息。
 * 解决方案：用token判断用户的会话是否一致，具体做法是根据userId在redis中查询是否有对应的token，如果有并且这个token和用户从前端传过来的token是匹配的，
 * 则表示当前的操作就是当前会话的用户发起的请求，则让请求通过，否则直接拒绝即可
 * Created by yw
 * 2021/3/17
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String USER_TOKEN_REDIS = "user_token";

    @Autowired
    private RedisOperator redisOperator;

    //拦截请求，在访问Controller之前
    //true：请求在经过验证以后是可以放行的
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("进入拦截器，被拦截...");

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(USER_TOKEN_REDIS + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)) {
                System.out.println("请登录...");
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    //先在A处（换个浏览器就能行）登录，再在B处登录，然后在A处访问拦截器中的请求，A处就会被要求重新登录
                    //因为B处的登录修改了redis中的token，A处再访问从前端传过来的token就过时了
                    System.out.println("账号在异地登录...");
                    return false;
                }
            }
        } else {
            System.out.println("请登录...");
            return false;
        }
        return true;
    }

    //在访问Controller之后，渲染视图之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //在访问Controller之后，渲染视图之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
