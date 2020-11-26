package cn.coderap.pojo.vo;

/**
 * 用于展示商品搜索列表结果的VO
 * Created by yw
 * 2020/11/26
 */
public class SearchItemsVO {

    private Integer itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    //数据库中的价格都是以分为单位进行保存的，前端会除以100进行转换
    private Integer price;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getSellCounts() {
        return sellCounts;
    }

    public void setSellCounts(Integer sellCounts) {
        this.sellCounts = sellCounts;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
