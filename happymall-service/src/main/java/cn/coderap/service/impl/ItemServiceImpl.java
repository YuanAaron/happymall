package cn.coderap.service.impl;

import cn.coderap.mapper.ItemsImgMapper;
import cn.coderap.mapper.ItemsMapper;
import cn.coderap.mapper.ItemsParamMapper;
import cn.coderap.mapper.ItemsSpecMapper;
import cn.coderap.pojo.Items;
import cn.coderap.pojo.ItemsImg;
import cn.coderap.pojo.ItemsParam;
import cn.coderap.pojo.ItemsSpec;
import cn.coderap.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> getItemsImgList(String itemId) {
        Example itemsImgExample=new Example(ItemsImg.class);
        Example.Criteria criteria = itemsImgExample.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsImgMapper.selectByExample(itemsImgExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> getItemSpecList(String itemId) {
        Example itemsSpecExample=new Example(ItemsSpec.class);
        Example.Criteria criteria = itemsSpecExample.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsSpecMapper.selectByExample(itemsSpecExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam getItemParam(String itemId) {
        Example itemsParamExample=new Example(ItemsParam.class);
        Example.Criteria criteria = itemsParamExample.createCriteria();
        criteria.andEqualTo("itemId",itemId);
        return itemsParamMapper.selectOneByExample(itemId);
    }
}
