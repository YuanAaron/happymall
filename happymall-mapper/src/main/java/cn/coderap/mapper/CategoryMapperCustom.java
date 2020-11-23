package cn.coderap.mapper;

import cn.coderap.pojo.vo.CategoryVO;
import cn.coderap.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCategoryList(Integer rootCategoryId);

    public List<NewItemsVO> lazyGetSixNewItems(@Param("paramsMap") Map<String,Object> map);
}