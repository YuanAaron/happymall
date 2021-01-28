package cn.coderap.service.center;

import cn.coderap.pojo.OrderItems;
import cn.coderap.pojo.bo.center.OrderItemsCommentBO;
import cn.coderap.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {

    /**
     * 根据订单id查询关联的商品
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户的评论
     * @param userId
     * @param orderId
     * @param commentList
     */
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList);

    /**
     * 查询我的历史评价列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);
}
