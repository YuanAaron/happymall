package cn.coderap.controller.center;

import cn.coderap.controller.BaseController;
import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.pojo.OrderItems;
import cn.coderap.pojo.Orders;
import cn.coderap.service.center.MyCommentsService;
import cn.coderap.service.center.MyOrdersService;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yw
 * 2021/1/25
 */
@Api(value = "个人中心-评价模块", tags = {"个人中心-评价模块相关接口"})
@RestController
@RequestMapping("/mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @ApiOperation(value = "查询所有订单列表",notes = "查询所有订单列表",httpMethod = "POST")
    @PostMapping("/pending")
    public JSONResult pending(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id",required = true)
            @RequestParam String orderId) {

        if (StringUtils.isBlank(userId)) {
            JSONResult.errorMsg("用户id不能为空");
        }
        if (StringUtils.isBlank(orderId)) {
            JSONResult.errorMsg("订单id不能为空");
        }

        //判断用户和订单是否关联
        JSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }

        //判断该笔订单是否已经评价过，评价过了就不再继续
        Orders order = (Orders)result.getData();
        if (order.getIsComment() == YesOrNoEnum.YES.type) {
            JSONResult.errorMsg("该笔订单已评价");
        }

        List<OrderItems> orderItemList = myCommentsService.queryPendingComment(orderId);
        return JSONResult.ok(orderItemList);
    }

}
