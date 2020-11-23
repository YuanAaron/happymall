package cn.coderap.service.impl;

import cn.coderap.enums.CategoryEnum;
import cn.coderap.mapper.CategoryMapper;
import cn.coderap.mapper.CategoryMapperCustom;
import cn.coderap.pojo.Category;
import cn.coderap.pojo.vo.CategoryVO;
import cn.coderap.pojo.vo.NewItemsVO;
import cn.coderap.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootCategory() {
        Example categoryExample=new Example(Category.class);
        Example.Criteria categoryCriteria = categoryExample.createCriteria();
        categoryCriteria.andEqualTo("type", CategoryEnum.ONE.type);
        List<Category> list = categoryMapper.selectByExample(categoryExample);
        return list;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCategoryList(Integer rootCategoryId) {
        return categoryMapperCustom.getSubCategoryList(rootCategoryId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<NewItemsVO> lazyGetSixNewItems(Integer rootCategoryId) {
        Map<String,Object> map=new HashMap<>();
        map.put("rootCategoryId", rootCategoryId);
        return categoryMapperCustom.lazyGetSixNewItems(map);
    }
}
