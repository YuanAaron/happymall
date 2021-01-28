package cn.coderap.service.impl.center;

import cn.coderap.enums.OrderStatusEnum;
import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.mapper.OrderStatusMapper;
import cn.coderap.mapper.OrdersMapper;
import cn.coderap.mapper.OrdersMapperCustom;
import cn.coderap.pojo.OrderStatus;
import cn.coderap.pojo.Orders;
import cn.coderap.pojo.vo.MyOrdersVO;
import cn.coderap.service.center.MyOrdersService;
import cn.coderap.service.impl.BaseService;
import cn.coderap.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw
 * 2021/1/25
 */
@Service
public class MyOrdersServiceImpl extends BaseService implements MyOrdersService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        //这里的判断好像没必要吧
        if (orderStatus != null) {
            map.put("orderStatus", orderStatus);
        }
        PageHelper.startPage(page, pageSize);
        List<MyOrdersVO> myOrdersVOList = ordersMapperCustom.queryMyOrders(map);
        return setterPagedGrid(myOrdersVOList, page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        orderStatus.setDeliverTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type); //该条件不能少
        orderStatusMapper.updateByExampleSelective(orderStatus, example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders order = new Orders();
        order.setId(orderId);
        order.setUserId(userId);
        order.setIsDelete(YesOrNoEnum.NO.type); //未删除
        return ordersMapper.selectOne(order);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type); //该条件不能少
        int res = orderStatusMapper.updateByExampleSelective(orderStatus, example);
        return res == 1 ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String userId, String orderId) {
        //自己添加：订单状态为交易关闭时，才能进行删除
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);
        OrderStatus orderStatusRes = orderStatusMapper.selectOne(orderStatus);
        if (orderStatusRes==null) {
            return false;
        }

        Orders order = new Orders();
        order.setIsDelete(YesOrNoEnum.YES.type);
        order.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);
        int res = ordersMapper.updateByExampleSelective(order, example);
        return res == 1 ? true : false;
    }
}
