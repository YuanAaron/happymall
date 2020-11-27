package cn.coderap.mapper;

import cn.coderap.pojo.vo.ItemCommentVO;
import cn.coderap.pojo.vo.SearchItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yw
 * 2020/11/26
 */
public interface ItemsMapperCustom {

    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String,Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String,Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String,Object> map);
}
