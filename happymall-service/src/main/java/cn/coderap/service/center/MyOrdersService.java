package cn.coderap.service.center;

import cn.coderap.utils.PagedGridResult;

public interface MyOrdersService {

    /**
     * 查询订单列表
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    /**
     * 更该订单状态（商家发货）
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);
}
