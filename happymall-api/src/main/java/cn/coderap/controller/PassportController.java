package cn.coderap.controller;

import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.UserBO;
import cn.coderap.service.UserService;
import cn.coderap.utils.CookieUtils;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import cn.coderap.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yw
 * 2020/11/9
 */
@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("/passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {
        //1、用户名不能为空
        if (StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名不能为空");
        }
        //2、查询注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已存在");
        }
        //3、请求成功，用户名没有重复
        return JSONResult.ok();
    }

    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/register")
    public JSONResult register(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response) { //TODO @ModelAttribute和@RequestBody区别
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        //校验
        //1、判断用户名和密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        //2、判断用户名是否已存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return JSONResult.errorMsg("用户名已存在");
        }
        //3、密码长度不能少于6位
        if (password.length()<6) {
            return JSONResult.errorMsg("密码长度不能少于6");
        }
        //4、判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return JSONResult.errorMsg("两次密码输入不一致");
        }
        //5、实现注册
        Users userRes=userService.createUser(userBO);
        userRes=setNullProperty(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userRes),true);

        //TODO 在分布式会话中，生成用户token，存入redis会话
        //TODO 在分布式会话中，同步购物车数据

        return JSONResult.ok();
    }

    @ApiOperation(value = "用户登陆",notes = "用户登陆",httpMethod = "POST")
    @PostMapping("/login")
    public JSONResult login(@RequestBody UserBO userBO,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //校验
        //1、判断用户名和密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return JSONResult.errorMsg("用户名或密码不能为空");
        }
        //2、实现登陆
        Users userRes = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userRes==null) {
            return JSONResult.errorMsg("用户名或密码不正确");
        }
        //学习京东：先登录，然后清除cookie(检查-Application-Cookies-https://www.jd.com-右键-clear)，刷新京东首页，发现需要再次登录，
        //证明在你登录后，京东将一些用户信息加密后放在cookie中，本项目也通过这种方式实现。

        //登录后，直接将userRes放到cookie中，但需要去除一部分敏感信息，比如password、realname等，一种方法是在Users的这些属性上使用
        //@JsonIgnore注解，这样当把这个实体类封装为JsonObject返回给前端时，不会显示这些属性（缺点：要修改从数据库逆向生成的原始实体
        //类，不建议）；方法二：在此处将这些属性直接设置为null
        userRes=setNullProperty(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userRes),true);

        //TODO 在分布式会话中，生成用户token，存入redis会话
        //TODO 在分布式会话中，同步购物车数据

        return JSONResult.ok(userRes);
    }

    private Users setNullProperty(Users userRes) {
        userRes.setPassword(null);
        userRes.setRealname(null);
        userRes.setMobile(null);
        userRes.setEmail(null);
        userRes.setSex(null);
        userRes.setBirthday(null);
        userRes.setCreatedTime(null);
        userRes.setUpdatedTime(null);
        return userRes;
    }

    @ApiOperation(value = "用户退出登陆",notes = "用户退出登陆",httpMethod = "POST")
    @PostMapping("/logout")
    public JSONResult logout(@RequestParam String userId,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        //清除用户信息相关的cookie
        CookieUtils.deleteCookie(request,response,"user");

        //后续
        //TODO 用户退出登录时，需要清空购物车（不是很懂）
        //TODO 在分布式会话中，需要清除用户数据（不是很懂）

        return JSONResult.ok();
    }

}
