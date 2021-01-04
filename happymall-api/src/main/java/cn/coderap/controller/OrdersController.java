package cn.coderap.controller;

import cn.coderap.enums.PayMethodEnum;
import cn.coderap.pojo.bo.SubmitOrderBO;
import cn.coderap.service.OrderService;
import cn.coderap.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(value = "订单相关",tags = {"订单相关的接口"})
@RestController
@RequestMapping("/orders")
public class OrdersController {

    /**
     * 1、创建订单
     * 2、创建订单后，移除购物车中已结算（已提交）的商品
     * 3、向支付中心（已经封装好的，再由支付中心向微信等发送支付请求）发送当前订单，用于保存支付中心的订单数据？？？
     */

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户提交订单",notes = "用户提交订单",httpMethod = "POST")
    @PostMapping("/create")
    public JSONResult create(@ApiParam(name = "submitOrderBO",value ="用于创建订单的BO",required = true)
                                  @RequestBody SubmitOrderBO submitOrderBO) {
        Integer payMethod = submitOrderBO.getPayMethod();
        if (payMethod != PayMethodEnum.WECHAT.type && payMethod != PayMethodEnum.ALIPAY.type) {
            JSONResult.errorMsg("支付方式不支持！");
        }

        //1、创建订单
        orderService.createOrder(submitOrderBO);

        return JSONResult.ok();
    }

}
