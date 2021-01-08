package cn.coderap.service;

import cn.coderap.pojo.OrderStatus;
import cn.coderap.pojo.bo.SubmitOrderBO;
import cn.coderap.pojo.vo.OrdersVO;

public interface OrderService {

    /**
     * 用于创建订单
     */
    public OrdersVO createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);
}
