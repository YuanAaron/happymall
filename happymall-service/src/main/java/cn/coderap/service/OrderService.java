package cn.coderap.service;

import cn.coderap.pojo.bo.SubmitOrderBO;

public interface OrderService {

    /**
     * 用于创建订单
     */
    public void createOrder(SubmitOrderBO submitOrderBO);
}
