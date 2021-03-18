package cn.coderap.controller.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器使用的必要性：
 * 基于权限的拦截：正常情况下，用户登录后修改用户信息，但是如果不拦截判断用户修改的是否是本人信息，那么别人就可以通过
 * 其他的某种手段（我用postman试过）来更改非自己的用户信息。
 * 解决方案：用token判断用户的会话是否一致
 * Created by yw
 * 2021/3/17
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    //拦截请求，在访问Controller之前
    //true：请求在经过验证以后是可以放行的
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入拦截器，被拦截...");
        return false;
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
