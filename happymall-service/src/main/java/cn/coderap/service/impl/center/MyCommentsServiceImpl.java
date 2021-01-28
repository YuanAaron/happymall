package cn.coderap.service.impl.center;

import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.mapper.ItemsCommentsMapperCustom;
import cn.coderap.mapper.OrderItemsMapper;
import cn.coderap.mapper.OrderStatusMapper;
import cn.coderap.mapper.OrdersMapper;
import cn.coderap.pojo.OrderItems;
import cn.coderap.pojo.OrderStatus;
import cn.coderap.pojo.Orders;
import cn.coderap.pojo.bo.center.OrderItemsCommentBO;
import cn.coderap.pojo.vo.MyCommentVO;
import cn.coderap.service.center.MyCommentsService;
import cn.coderap.service.impl.BaseService;
import cn.coderap.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw
 * 2021/1/25
 */
@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems orderItems = new OrderItems();
        orderItems.setOrderId(orderId);
        return orderItemsMapper.select(orderItems);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList) {

        //1、保存评价 items_comments
        //为主键赋值
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("commentList", commentList);
        //批量插入
        itemsCommentsMapperCustom.saveComments(map);

        //2、修改订单表为已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNoEnum.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        //3、修改订单状态中的评论时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);

        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> myCommentVOList = itemsCommentsMapperCustom.queryMyComments(map);
        return setterPagedGrid(myCommentVOList, page);
    }
}
