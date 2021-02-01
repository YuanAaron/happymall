package cn.coderap.mapper;

import cn.coderap.pojo.OrderStatus;
import cn.coderap.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String,Object> map);

    public Integer getMyOrderStatusCounts(@Param("paramsMap") Map<String,Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String,Object> map);
}
