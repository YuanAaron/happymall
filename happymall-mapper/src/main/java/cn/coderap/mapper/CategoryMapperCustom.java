package cn.coderap.mapper;

import cn.coderap.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCategoryList(Integer rootCategoryId);
}