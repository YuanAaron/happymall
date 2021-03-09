package cn.coderap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ApiIgnore
@RestController
public class HelloController {

    private final static Logger logger= LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public String hello() {
        logger.debug("debug: hello");
        logger.info("info: hello");
        logger.warn("warn: hello");
        logger.error("error: hello");
        return "hello world";
    }

    /**
     * 使用redis实现用户的分布式会话和使用Spring Session实现用户的分布式会话:
     * 1、推荐前者
     * 2、spring session与spring耦合，如果有其他模块用C++、Go等开发，想要获取spring session比较复杂
     */

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600); //设置过期时间
        session.getAttribute("userInfo");
//        session.removeAttribute("userInfo");
        return "ok";
    }
}
