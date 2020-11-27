package cn.coderap.controller;

import cn.coderap.pojo.bo.ShopcartItemBO;
import cn.coderap.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yw
 * 2020/11/27
 */
@Api(value = "购物车接口Controller",tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("/shopcart")
public class ShopcartController {

    private static final Logger logger= LoggerFactory.getLogger(ShopcartController.class);

    @ApiOperation(value = "同步购物车到后端",notes = "同步购物车到后端",httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(
            @ApiParam(name = "userId",value ="用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "shopcartItemBO",value ="购物车商品对象BO",required = true)
            @RequestBody ShopcartItemBO shopcartItemBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        //用于购物车测试
        logger.info(shopcartItemBO.toString());

        // 加入购物车分为登录、未登录两种情况，未登录时通过cookie实现购物车（前端实现），已登录时，同样
        // 通过cookie实现购物车（前端实现），但同时在后端同步购物车到redis以适用分布式
        //TODO 在登录的情况下，用户在前端添加商品到购物车，会同时在后端同步购物车到redis

        return JSONResult.ok();
    }

}
