package cn.coderap.controller;

import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.UserBO;
import cn.coderap.pojo.vo.UsersVO;
import cn.coderap.service.UserService;
import cn.coderap.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class HelloController {

    private final static Logger logger= LoggerFactory.getLogger(HelloController.class);
    public static final String USER_TOKEN_REDIS = "user_token";

    @Resource
    private UserService userService;

    @Resource
    private RedisOperator redisOperator;

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl",returnUrl);
        //TODO 后续完善是否登录校验

        //如果用户从未登录过，第一次访问则跳转到CAS的统一登录页面
        return "login";
    }

    @PostMapping("/doLogin")
    public String login(String username,
                            String password,
                            String returnUrl,
                            Model model,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        //这个必须放在最前面
        model.addAttribute("returnUrl",returnUrl);

        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg","用户名或密码不能为空");
            return "login";
        }

        Users userRes = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userRes==null) {
            model.addAttribute("errmsg","用户名或密码不正确");
            return "login";
        }

        //创建用户会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userRes, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(USER_TOKEN_REDIS + ":" + userRes.getId(), JsonUtils.objectToJson(usersVO));
        return "login";
    }
}
