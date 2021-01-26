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
}
