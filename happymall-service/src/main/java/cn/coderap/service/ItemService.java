package cn.coderap.service;

import cn.coderap.pojo.Items;
import cn.coderap.pojo.ItemsImg;
import cn.coderap.pojo.ItemsParam;
import cn.coderap.pojo.ItemsSpec;
import cn.coderap.pojo.vo.CommentLevelCountVO;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品id查询商品详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id获取商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> getItemsImgList(String itemId);

    /**
     * 根据商品id获取商品规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> getItemSpecList(String itemId);

    /**
     * 根据商品id获取商品参数
     * @param itemId
     * @return
     */
    public ItemsParam getItemParam(String itemId);

    /**
     * 根据商品id查询不同评级等级的数量
     * @param itemId
     */
    public CommentLevelCountVO queryCommentCount(String itemId);

}
