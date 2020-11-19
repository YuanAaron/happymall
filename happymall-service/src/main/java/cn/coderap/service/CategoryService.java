package cn.coderap.service;

import cn.coderap.pojo.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 查询所有一级分类
     * @return
     */
    public List<Category> queryAllRootCategory();
}
