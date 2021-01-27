package cn.coderap.service.center;

import cn.coderap.pojo.Orders;
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

    /**
     * 查询订单（校验userId和orderId有关系）
     * @param userId
     * @param orderId
     */
    public Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态（确认收货）
     * @param orderId
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId, String orderId);
}
