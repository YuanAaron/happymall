package cn.coderap.controller.center;

import cn.coderap.pojo.Users;
import cn.coderap.service.center.CenterUserService;
import cn.coderap.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yw
 * 2021/1/25
 */
@Api(value = "center - 用户中心", tags = {"用户中心展示的相关接口"})
@RestController
@RequestMapping("/center")
public class CenterController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",httpMethod = "GET")
    @GetMapping("/userInfo")
    public JSONResult userInfo(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId) {
        Users user = centerUserService.queryUserInfo(userId);
        return JSONResult.ok(user);
    }
}
