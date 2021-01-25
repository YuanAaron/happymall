package cn.coderap.controller.center;

import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.center.CenterUserBO;
import cn.coderap.service.center.CenterUserService;
import cn.coderap.utils.CookieUtils;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw
 * 2021/1/25
 */
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("/userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "更新用户信息",notes = "更新用户信息",httpMethod = "POST")
    @PostMapping("/update")
    public JSONResult update(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "centerUserBO",value = "用户传入的数据封装在此entity中",required = true)
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response) {

        //判断result中是否包含错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return JSONResult.ok(errorMap);
        }

        Users userRes = centerUserService.updateUserInfo(userId, centerUserBO);
        //更新Users后，覆盖cookie
        userRes = setNullProperty(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userRes),true);

        //TODO 在分布式会话中，增加令牌token，整合进redis

        return JSONResult.ok();
    }

    //处理错误信息
    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrorList = result.getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            //发生验证错误的属性
            String errorField = fieldError.getField();
            //验证错误信息
            String errorMessage = fieldError.getDefaultMessage();
            map.put(errorField, errorMessage);
        }
        return map;
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
}
