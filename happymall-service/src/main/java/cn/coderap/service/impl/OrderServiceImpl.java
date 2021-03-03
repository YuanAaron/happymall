package cn.coderap.service.impl;

import cn.coderap.enums.OrderStatusEnum;
import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.mapper.OrderItemsMapper;
import cn.coderap.mapper.OrderStatusMapper;
import cn.coderap.mapper.OrdersMapper;
import cn.coderap.pojo.*;
import cn.coderap.pojo.bo.ShopcartItemBO;
import cn.coderap.pojo.bo.SubmitOrderBO;
import cn.coderap.pojo.vo.MerchantOrdersVO;
import cn.coderap.pojo.vo.OrdersVO;
import cn.coderap.service.AddressService;
import cn.coderap.service.ItemService;
import cn.coderap.service.OrderService;
import cn.coderap.utils.DateUtil;
import cn.coderap.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrdersVO createOrder(SubmitOrderBO submitOrderBO, List<ShopcartItemBO> shopcartList) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer postAmount = 0; //包邮

        //1、新订单数据保存到orders表
        Orders orders = new Orders();
        String orderId = sid.nextShort();
        orders.setId(orderId);
        orders.setUserId(userId);

        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);
        orders.setReceiverName(userAddress.getReceiver());
        orders.setReceiverMobile(userAddress.getMobile());
        orders.setReceiverAddress(userAddress.getProvince() + " " + userAddress.getCity() +
                " " + userAddress.getDistrict() + " " + userAddress.getDetail());

        orders.setPostAmount(postAmount);
        orders.setPayMethod(payMethod);
        orders.setLeftMsg(leftMsg);
        orders.setIsComment(YesOrNoEnum.NO.type);
        orders.setIsDelete(YesOrNoEnum.NO.type);
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());

        //2、遍历itemSpecIds将订单商品数据保存到order_items
        String[] ids = itemSpecIds.split(",");
        Integer totalAmount = 0; //订单总价格
        Integer realPayAmount = 0; //实际支付总价格
        for (String id : ids) {
            //整合redis后，商品购买的数量重新从redis购物车中获取
            //Integer buyCounts = 1; //测试用
            ShopcartItemBO shopcartItemBO = getbuyCountsFromShopcart(shopcartList, id);
            Integer buyCounts = shopcartItemBO.getBuyCounts();

            //2.1 根据规格id查询规格的具体信息
            ItemsSpec itemsSpec = itemService.queryItemsSpecById(id);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;

            //2.2 根据商品id获取商品信息及商品图片
            String itemId = itemsSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgByItemId(itemId);

            //2.3 将订单商品数据保存到数据库
            OrderItems orderItems = new OrderItems();
            String orderItemsId = sid.nextShort();
            orderItems.setId(orderItemsId);
            orderItems.setOrderId(orderId);
            orderItems.setItemId(itemId);
            orderItems.setItemImg(imgUrl);
            orderItems.setItemName(item.getItemName());
            orderItems.setBuyCounts(buyCounts);
            orderItems.setItemSpecId(id);
            orderItems.setItemSpecName(itemsSpec.getName());
            orderItems.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(orderItems);

            //2.4 在用户提交订单以后，规格表中需要扣减库存(重点)
            itemService.decreaseItemSpecStock(id, buyCounts);
        }

        orders.setTotalAmount(totalAmount);
        orders.setRealPayAmount(realPayAmount);
        ordersMapper.insert(orders);

        //3、保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //4、构建商户订单VO，用于传给支付中心的BO
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setPayMethod(payMethod);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);

        OrdersVO ordersVO = new OrdersVO();
        ordersVO.setOrderId(orderId);
        ordersVO.setMerchantOrdersVO(merchantOrdersVO);
        return ordersVO;
    }

    /**
     * 从redis中的购物车中里获取商品，目的是获取buyCounts
     * @param shopcartList
     * @param id
     * @return
     */
    private ShopcartItemBO getbuyCountsFromShopcart(List<ShopcartItemBO> shopcartList, String id) {
        for (ShopcartItemBO shopcartItemBO : shopcartList) {
            if (shopcartItemBO.getSpecId().equals(id)) {
                return shopcartItemBO;
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        //查询所有未付款订单，判断时间时间是否超时（1天），超时则关闭交易
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(orderStatus);
        for (OrderStatus os : list) {
            Date createdTime = os.getCreatedTime();
            int days = DateUtil.daysBetween(createdTime, new Date());
            if (days>=1) {
                doCloseOrder(os.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void doCloseOrder(String orderId) {
        OrderStatus os = new OrderStatus();
        os.setOrderId(orderId);
        os.setOrderStatus(OrderStatusEnum.CLOSE.type);
        os.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(os);
    }
}
