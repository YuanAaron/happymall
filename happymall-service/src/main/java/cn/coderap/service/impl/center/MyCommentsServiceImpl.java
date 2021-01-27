package cn.coderap.service.impl.center;

import cn.coderap.mapper.OrderItemsMapper;
import cn.coderap.pojo.OrderItems;
import cn.coderap.service.center.MyCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yw
 * 2021/1/25
 */
@Service
public class MyCommentsServiceImpl implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);
        return orderItemsMapper.select(orderItems);
    }
}
