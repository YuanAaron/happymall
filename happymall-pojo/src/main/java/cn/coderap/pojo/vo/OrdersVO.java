package cn.coderap.pojo.vo;

import cn.coderap.pojo.bo.ShopcartItemBO;

import java.util.List;

public class OrdersVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;

    private List<ShopcartItemBO> toBeRemovedShopcartItemBOList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }

    public List<ShopcartItemBO> getToBeRemovedShopcartItemBOList() {
        return toBeRemovedShopcartItemBOList;
    }

    public void setToBeRemovedShopcartItemBOList(List<ShopcartItemBO> toBeRemovedShopcartItemBOList) {
        this.toBeRemovedShopcartItemBOList = toBeRemovedShopcartItemBOList;
    }
}