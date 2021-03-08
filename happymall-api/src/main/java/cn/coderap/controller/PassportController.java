package cn.coderap.controller;

import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.ShopcartItemBO;
import cn.coderap.pojo.bo.UserBO;
import cn.coderap.pojo.vo.UsersVO;
import cn.coderap.service.UserService;
import cn.coderap.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yw
 * 2020/11/9
 */
@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("/passport")
public class PassportController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

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
//        userRes=setNullProperty(userRes); //因为使用了UsersVO，将需要放到cookie中的用户信息封装到了UsersVO中，这个方法也就没有必要了

        //在分布式会话中，生成用户token，存入redis会话
        //方案一：将uniqueToken直接放到一个新的cookie(user_token)中
        //方案二：将uniqueToken和用户信息封装到UsersVO中，然后放到cookie（user）中,这里采用第二种方案。
        UsersVO usersVO = convertToUsersVO(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO),true);
        //在分布式会话中，同步购物车数据
        synchShopcartData(userRes.getId(),request,response);

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
//        userRes=setNullProperty(userRes);//因为使用了UsersVO，将需要放到cookie中的用户信息封装到了UsersVO中，这个方法也就没有必要了

        //在分布式会话中，生成用户token，存入redis会话
        //原理同注册
        UsersVO usersVO = convertToUsersVO(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO),true);

        //在分布式会话中，同步购物车数据
        synchShopcartData(userRes.getId(),request,response);

        return JSONResult.ok(userRes);
    }

    /**
     * 注册/登录成功后，同步cookie购物车和redis购物车中的数据
     */
    private void synchShopcartData(String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        /**
         * 1、redis中没有数据
         *    1.1、如果cookie购物车为空，不做任何处理；
         *    1.2、如果cookie购物车不为空，直接放入redis中
         * 2、redis中有数据
         *    2.1、如果cookie购物车为空，将redis购物车数据覆盖本地cookie（比如公司电脑添加到购物车的数据，家里电脑登录后仍应存在）；
         *    2.2、如果cookie购物车不为空，合并cookie购物车和redis购物车中的商品数据。
         *         2.2.1、如果cookie购物车中的某个商品在redis中存在，则以cookie为主，用cookie中该商品的数量覆盖redis中该商品的数量（参考以前的京东，也是本项目的做法）,当然也可以累加。
         *         2.2.2、将cookie购物车中的对应商品（存在的商品）放到待删除list中（用于最后的删除和合并）
         *         2.2.3、将待删除列表中的商品从cookie购物车中删除
         *         2.2.4、将cookie购物车数据合并（添加）到redis购物车中
         *         2.2.5、用合并后的数据（最新数据）覆盖redis购物车中的数据以及本地cookie购物车中的数据，以保证两者是同步的。
         */

        //从redis中获取购物车
        String shopcartRedis = redisOperator.get(HAPPYMALL_SHOPCART + ":" + userId);
        //从cookie中获取购物车
        String shopcartCookie = CookieUtils.getCookieValue(request, HAPPYMALL_SHOPCART, true);
        if (StringUtils.isBlank(shopcartRedis)) {
            //redis为空，cookie不为空，直接把cookie中的数据同步到redis中
            if (StringUtils.isNotBlank(shopcartCookie)) {
                redisOperator.set(HAPPYMALL_SHOPCART + ":" + userId, shopcartCookie);
            }
        } else {
            //redis不为空，cookie也不为空，合并cookie和redis中的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartCookie)){
                List<ShopcartItemBO> shopcartListCookie = JsonUtils.jsonToList(shopcartCookie, ShopcartItemBO.class);
                List<ShopcartItemBO> shopcartListRedis = JsonUtils.jsonToList(shopcartRedis, ShopcartItemBO.class);
                //1、cookie和redis中都已经存在的商品，用cookie购物车中对应商品的数量覆盖redis购物车中对应商品的数量
                //2、将cookie购物车中的对应商品（存在的商品）放到待删除list中（用于最后的删除和合并）
                List<ShopcartItemBO> toBeRemovedListCookie = new ArrayList<>(); //定义一个待删除的list
                for (ShopcartItemBO itemRedis : shopcartListRedis) {
                    String idRedis = itemRedis.getSpecId();
                    for (ShopcartItemBO itemCookie : shopcartListCookie) {
                        String idCookie = itemCookie.getSpecId();
                        if (idRedis.equals(idCookie)) {
                            //用cookie中该商品的购买数量覆盖redis中的该商品的购买数量
                            itemRedis.setBuyCounts(itemCookie.getBuyCounts());
                            //将itemCookie放到待删除list中
                            toBeRemovedListCookie.add(itemCookie);
                            break;
                        }
                    }
                }
                //3、从cookie购物车中删除待删除列表中的商品
                shopcartListCookie.removeAll(toBeRemovedListCookie);
                //4、将cookie购物车数据合并（添加）到redis购物车中
                shopcartListRedis.addAll(shopcartListCookie);
                //5、用合并后的数据（最新数据）覆盖redis购物车中的数据以及本地cookie购物车中的数据
                CookieUtils.setCookie(request, response, HAPPYMALL_SHOPCART, JsonUtils.objectToJson(shopcartListRedis),true);
                redisOperator.set(HAPPYMALL_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else{
                //redis不为空，cookie为空，直接将redis购物车数据覆盖本地cookie
                CookieUtils.setCookie(request, response, HAPPYMALL_SHOPCART, shopcartRedis,true);
            }
        }

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
        //在分布式会话中，需要清除redis中的会话信息（token）
        redisOperator.del(USER_TOKEN_REDIS + ":" + userId);

        //用户退出登录时，需要清空cookie购物车
        CookieUtils.deleteCookie(request, response, HAPPYMALL_SHOPCART);

        return JSONResult.ok();
    }

}
