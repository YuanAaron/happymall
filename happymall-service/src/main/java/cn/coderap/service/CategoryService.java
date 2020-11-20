package cn.coderap.service;

import cn.coderap.pojo.Category;
import cn.coderap.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    /**
     * 查询所有一级分类
     * @return
     */
    public List<Category> queryAllRootCategory();

    /**
     * 根据一级大分类的rootCategoryId查询二级分类及其对应的三级子分类
     * @param rootCategoryId
     * @return
     */
    public List<CategoryVO> getSubCategoryList(Integer rootCategoryId);
}
