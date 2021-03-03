package cn.coderap.controller;

import cn.coderap.enums.OrderStatusEnum;
import cn.coderap.enums.PayMethodEnum;
import cn.coderap.pojo.OrderStatus;
import cn.coderap.pojo.bo.ShopcartItemBO;
import cn.coderap.pojo.bo.SubmitOrderBO;
import cn.coderap.pojo.vo.MerchantOrdersVO;
import cn.coderap.pojo.vo.OrdersVO;
import cn.coderap.service.OrderService;
import cn.coderap.utils.CookieUtils;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import cn.coderap.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Api(value = "订单相关",tags = {"订单相关的接口"})
@RestController
@RequestMapping("/orders")
public class OrdersController extends BaseController{

    /**
     * 1、创建订单
     * 2、创建订单后，移除购物车中已结算（已提交）的商品
     * 3、向支付中心（已经封装好的，再由支付中心向微信等发送支付请求）发送当前订单
     */

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户提交订单",notes = "用户提交订单",httpMethod = "POST")
    @PostMapping("/create")
    public JSONResult create(@ApiParam(name = "submitOrderBO",value ="用于创建订单的BO",required = true)
                                  @RequestBody SubmitOrderBO submitOrderBO,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        Integer payMethod = submitOrderBO.getPayMethod();
        if (payMethod != PayMethodEnum.WECHAT.type && payMethod != PayMethodEnum.ALIPAY.type) {
            JSONResult.errorMsg("支付方式不支持！");
        }

        String shopcart = redisOperator.get(HAPPYMALL_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopcart)) {
            return JSONResult.errorMsg("购物车数据不正确");
        }
        List<ShopcartItemBO> shopcartList = JsonUtils.jsonToList(shopcart, ShopcartItemBO.class);
        //1、创建订单
        OrdersVO ordersVO = orderService.createOrder(submitOrderBO,shopcartList);
        String orderId = ordersVO.getOrderId();

        //2、创建订单后，移除购物车中已结算（已提交）的商品
        //整合redis后，完善购物车中已结算商品的清除，并同步到前端的cookie
        shopcartList.removeAll(ordersVO.getToBeRemovedShopcartItemBOList());
        redisOperator.set(HAPPYMALL_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopcartList));
        CookieUtils.setCookie(request, response, HAPPYMALL_SHOPCART, JsonUtils.objectToJson(shopcartList),true);

        //3、向支付中心（已经封装好的，再由支付中心向微信等发送支付请求）发送当前订单
        // 如何发起一个Rest请求调用其他项目中rest风格的接口呢？ //http 或 springMVC提供的RestTemplate
        MerchantOrdersVO merchantOrdersVO = ordersVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);
        merchantOrdersVO.setAmount(1); //单纯为了测试，将所有的支付金额都统一改为一分钱（正常情况下去掉即可）

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId","imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO,headers);
        ResponseEntity<JSONResult> responseEntity = restTemplate.postForEntity(paymentUrl, entity, JSONResult.class);
        JSONResult paymentRes = responseEntity.getBody();
        if (paymentRes.getStatus() != 200) {
            return JSONResult.errorMsg("支付中心订单创建失败，请联系管理员!");
        }
        return JSONResult.ok(orderId);
    }

    @ApiOperation(value = "支付中心通知（回调）商户端的接口",notes = "支付中心通知商户端修改订单状态",httpMethod = "POST")
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @ApiOperation(value = "支付中心通知（回调）商户端的接口",notes = "支付中心通知商户端修改订单状态",httpMethod = "POST")
    @PostMapping("/getPaidOrderInfo")
    public JSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return JSONResult.ok(orderStatus);
    }
}
