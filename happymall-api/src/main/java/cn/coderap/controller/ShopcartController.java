package cn.coderap.controller;

import cn.coderap.pojo.bo.ShopcartItemBO;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import cn.coderap.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yw
 * 2020/11/27
 */
@Api(value = "购物车接口Controller",tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("/shopcart")
public class ShopcartController extends BaseController{

    private static final Logger logger= LoggerFactory.getLogger(ShopcartController.class);

    @Autowired
    private RedisOperator redisOperator;

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

        //在登录的情况下，用户在前端添加商品到购物车，会同时在后端同步购物车到redis
        //此外，需要判断购物车中是否已经存在某商品（redis中相应的键是否存在），如果没有只需把商品添加到购物车，如果存在则累加购买数量
        String shopcart = redisOperator.get(HAPPYMALL_SHOPCART + ":" + userId);
        List<ShopcartItemBO> shopcartList = null;
        if (StringUtils.isBlank(shopcart)) {
            //redis中没有购物车
            shopcartList = new ArrayList<>();
            //添加商品到购物车
            shopcartList.add(shopcartItemBO);
        } else {
            //redis中已经有购物车了
            shopcartList = JsonUtils.jsonToList(shopcart, ShopcartItemBO.class);
            //判断购物车中是否存在该商品，如果没有只需把商品添加到购物车，如果有的话累加购买数量
            boolean isHaving = false;
            for (ShopcartItemBO sib : shopcartList) {
                if (sib.getSpecId().equals(shopcartItemBO.getSpecId())) {
                    sib.setBuyCounts(sib.getBuyCounts()+shopcartItemBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopcartList.add(shopcartItemBO);
            }
        }
        //创建（没有购物车）或更新（存在购物车）购物车
        redisOperator.set(HAPPYMALL_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        return JSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品",notes = "从购物车中删除商品",httpMethod = "POST")
    @PostMapping("/del")
    public JSONResult del(
            @ApiParam(name = "userId",value ="用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "商品规格id",value ="商品规格id",required = true)
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JSONResult.errorMsg("参数不能为空");
        }

        //删除购物车中的商品分为登录、未登录两种情况，未登录时只需删除cookie购物车中的对应商品（前端实现），
        // 已登录时，不仅要删除cookie购物车中的对应商品（前端实现），同时后端同步删除redis购物车中的对应商品
        String shopcart = redisOperator.get(HAPPYMALL_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopcart)) {
            //redis中已经有购物车了
            List<ShopcartItemBO> shopcartList = JsonUtils.jsonToList(shopcart, ShopcartItemBO.class);
            //判断购物车中是否存在该商品，如果有的话则删除
//            for (ShopcartItemBO sib : shopcartList) {
//                if (sib.getSpecId().equals(itemSpecId)) {
//                    //删除元素后继续循环会报ConcurrentModificationException，因为元素在使用的时候发生了并发的修改，导致异常抛出。
//                    //但是删除完毕马上使用break跳出，则不会触发报错。
//                    shopcartList.remove(sib);
//                    break;
//                }
//            }

            //即使去掉break，使用迭代器也可以正常的循环和删除，注意：使用iterator.remove()，如果用list的remove方法同样会报上ConcurrentModificationException
            Iterator<ShopcartItemBO> it = shopcartList.iterator();
            while (it.hasNext()) {
                if (it.next().getSpecId().equals(itemSpecId)) {
                    it.remove();
                    break;
                }
            }

            //更新购物车
            redisOperator.set(HAPPYMALL_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartList));
        }

        return JSONResult.ok();
    }

}
