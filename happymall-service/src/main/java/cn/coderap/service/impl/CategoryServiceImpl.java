package cn.coderap.service.impl;

import cn.coderap.enums.CategoryEnum;
import cn.coderap.mapper.CategoryMapper;
import cn.coderap.pojo.Category;
import cn.coderap.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> queryAllRootCategory() {
        Example categoryExample=new Example(Category.class);
        Example.Criteria categoryCriteria = categoryExample.createCriteria();
        categoryCriteria.andEqualTo("type", CategoryEnum.ONE.type);
        List<Category> list = categoryMapper.selectByExample(categoryExample);
        return list;
    }
}
