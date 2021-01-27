package cn.coderap.controller.center;

import cn.coderap.controller.BaseController;
import cn.coderap.service.center.MyOrdersService;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yw
 * 2021/1/25
 */
@Api(value = "个人中心-订单管理接口", tags = {"个人中心-订单管理相关接口"})
@RestController
@RequestMapping("/myorders")
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询所有订单列表",notes = "查询所有订单列表",httpMethod = "POST")
    @PostMapping("/query")
    public JSONResult query(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态")
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page",value ="当前页数",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value ="每页显示条数",required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户不存在"); //null
        }

        if (page==null) {
            page=1;
        }

        if (pageSize==null) {
            pageSize= COMMEN_PAGE_SIZE;
        }
        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return JSONResult.ok(grid);
    }

    //因为没有商家发货的后端，所以这个接口只是用于模拟
    @ApiOperation(value = "商家发货",notes = "商家发货",httpMethod = "GET")
    @GetMapping("/deliver")
    public JSONResult deliver(
            @ApiParam(name = "orderId",value = "订单id",required = true)
            @RequestParam String orderId) {

        if (StringUtils.isBlank(orderId)) {
            JSONResult.errorMsg("订单id不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return JSONResult.ok();
    }

}
