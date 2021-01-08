package cn.coderap.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    //调用生产环境的支付中心接口
    //在生产环境的orders表中生成一条记录，测试方法：postman访问http://payment.t.mukewang.com/foodie-payment/payment/getPaymentCenterOrderInfo?merchantOrderId=(orderId)&merchantUserId=(userId)
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    //微信支付成功后，通知支付中心，再由支付中心通知happymall平台；
    //这个是支付中心回调通知happymall的url
//    String payReturnUrl = "http://127.0.0.1:8088/orders/notifyMerchantOrderPaid";
    //内网穿透：将本地服务器放到公网，以供支付中心调用  注意：每次重启natapp该地址都会更改
    String payReturnUrl = "http://yywzs2.natappfree.cc/orders/notifyMerchantOrderPaid";



}
