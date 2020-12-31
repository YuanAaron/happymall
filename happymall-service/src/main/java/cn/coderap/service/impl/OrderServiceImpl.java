package cn.coderap.service.impl;

import cn.coderap.enums.OrderStatusEnum;
import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.mapper.OrderItemsMapper;
import cn.coderap.mapper.OrderStatusMapper;
import cn.coderap.mapper.OrdersMapper;
import cn.coderap.pojo.*;
import cn.coderap.pojo.bo.SubmitOrderBO;
import cn.coderap.service.AddressService;
import cn.coderap.service.ItemService;
import cn.coderap.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public void createOrder(SubmitOrderBO submitOrderBO) {
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
            //2.1 根据规格id查询规格的具体信息
            ItemsSpec itemsSpec = itemService.queryItemsSpecById(id);
            Integer buyCounts = 1; //TODO 整合redis后，商品购买的数量重新从redis购物车中获取
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
    }
}
