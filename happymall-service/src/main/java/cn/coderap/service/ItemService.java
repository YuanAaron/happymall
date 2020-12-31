package cn.coderap.service;

import cn.coderap.pojo.Items;
import cn.coderap.pojo.ItemsImg;
import cn.coderap.pojo.ItemsParam;
import cn.coderap.pojo.ItemsSpec;
import cn.coderap.pojo.vo.CommentLevelCountVO;
import cn.coderap.pojo.vo.ShopcartItemVO;
import cn.coderap.utils.PagedGridResult;

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

    /**
     * 根据商品id查询商品的评价（不同等级，分页）
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据三级小分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ids(拼接)查询最新的购物车中的商品数据(刷新购物车中的商品数据)
     * @param specIds 拼接的规格id
     * @return
     */
    public List<ShopcartItemVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据规格id查询规格对象
     * @param id
     * @return
     */
    public ItemsSpec queryItemsSpecById(String id);

    /**
     * 根据商品id查询商品主图url
     * @param itemId
     * @return
     */
    public String queryItemMainImgByItemId(String itemId);

    /**
     * 扣减库存
     * @param specId
     * @param buyCounts
     */
    public void decreaseItemSpecStock(String specId, int buyCounts);

}
