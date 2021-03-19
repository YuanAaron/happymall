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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class HelloController {

    private final static Logger logger= LoggerFactory.getLogger(HelloController.class);
    public static final String USER_TOKEN_REDIS = "user_token";

    public static final String USER_TICKET_REDIS = "user_ticket";
    public static final String USER_TICKET_COOKIE = "user_ticket_cookie";

    public static final String TMP_TICKET_REDIS = "tmp_ticket";

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

    /**
     * CAS的统一登录接口
     * 1、登录后创建用户的全局会话uniqueToken
     * 2、创建用户全局门票userTicket，用以表示用户在CAS端是否登录过
     * 3、创建用户的临时票据tmpTicket，用以回跳时回传
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
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

        //1、实现登录
        Users userRes = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userRes==null) {
            model.addAttribute("errmsg","用户名或密码不正确");
            return "login";
        }

        //2、创建用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userRes, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(USER_TOKEN_REDIS + ":" + userRes.getId(), JsonUtils.objectToJson(usersVO));

        //3、生成全局门票userTicket，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();
        //3.1、用户全局门票userTicket需要放到CAS端的cookie中(刚开始没有太理解！！！)
        setCookie(USER_TICKET_COOKIE,userTicket,response); //TODO 可以对userTicket进行base64加密
        //4、全局门票userTicket关联用户id,并放到到redis中（以动物园为例，表示这个用户有门票了，可以在各个景区游玩）
        redisOperator.set(USER_TICKET_REDIS + ":" + userTicket, userRes.getId());
        //5、生成临时票据tmpTicket（由CAS端签发的一个一次性的临时ticket），然后携带tmpTicket回跳到sso-mtv/sso-music网站(相当于动物园中的某些收费馆)
        String tmpTicket = createTmpTicket();

        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    /**
     * 使用一次性票据到CAS验证以获取用户会话
     * @param tmpTicket
     * @return
     */
    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public JSONResult verifyTmpTicket(String tmpTicket,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String tmpTicketValue = redisOperator.get(TMP_TICKET_REDIS + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            JSONResult.errorUserTicket("用户票据异常!");
        }

        //校验传过来的tmpTicket和redis中查到的tmpTicketValue是否一致
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            JSONResult.errorUserTicket("用户票据异常!");
        } else {
            //临时票据校验通过，临时票据使用完后，需要销毁
            redisOperator.del(TMP_TICKET_REDIS + ":" + tmpTicket);
        }

        //临时票据校验通过后，获取CAS端cookie中的全局门票userTicket，以此再获取用户会话

        return JSONResult.ok();
    }

    /**
     * 创建临时票据
     * @return
     */
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        //将临时票据保存到redis
        try {
            redisOperator.set(TMP_TICKET_REDIS + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket),600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key,String val,HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
