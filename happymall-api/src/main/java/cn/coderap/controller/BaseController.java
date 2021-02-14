package cn.coderap.controller;

import cn.coderap.pojo.Orders;
import cn.coderap.service.center.MyOrdersService;
import cn.coderap.utils.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class BaseController {

    public static final Integer COMMEN_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    //调用生产环境的支付中心接口
    //在生产环境的orders表中生成一条记录，测试方法：postman访问http://payment.t.mukewang.com/foodie-payment/payment/getPaymentCenterOrderInfo?merchantOrderId=(orderId)&merchantUserId=(userId)
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    //微信支付成功后，通知支付中心，再由支付中心通知happymall平台；
    //这个是支付中心回调通知happymall的url
//    String payReturnUrl = "http://127.0.0.1:8088/orders/notifyMerchantOrderPaid";
    //内网穿透：将本地服务器放到公网，以供支付中心调用  注意：每次重启natapp该地址都会更改
    //开发
    //String payReturnUrl = "http://4t7vwb.natappfree.cc/orders/notifyMerchantOrderPaid";
    //生产
    String payReturnUrl = "http://api.coderap.cn:8088/happymall/orders/notifyMerchantOrderPaid";

    //用户上传头像的位置
    //public static final String USER_FACE_IMAGE_LOCATION = File.separator + "upload" + File.separator + "happymall"+ File.separator + "images";


    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @param userId
     * @param orderId
     * @return
     */
    public JSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return JSONResult.errorMsg("订单不存在");
        }
        return JSONResult.ok(order); //order用于后面判断是否已评价
    }

}
