package cn.coderap.service.impl;

import cn.coderap.enums.CommentLevelEnum;
import cn.coderap.mapper.*;
import cn.coderap.pojo.*;
import cn.coderap.pojo.vo.CommentLevelCountVO;
import cn.coderap.pojo.vo.ItemCommentVO;
import cn.coderap.pojo.vo.SearchItemsVO;
import cn.coderap.pojo.vo.ShopcartItemVO;
import cn.coderap.service.ItemService;
import cn.coderap.utils.DesensitizationUtil;
import cn.coderap.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

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
        return itemsParamMapper.selectOneByExample(itemsParamExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountVO queryCommentCount(String itemId) {
        Integer goodCount=getCommentCount(itemId, CommentLevelEnum.GOOD.type);
        Integer normalCount=getCommentCount(itemId, CommentLevelEnum.NORMAL.type);
        Integer badCount=getCommentCount(itemId, CommentLevelEnum.BAD.type);
        Integer totalCount=goodCount+normalCount+badCount;

        CommentLevelCountVO commentLevelCountVO=new CommentLevelCountVO();
        commentLevelCountVO.setTotalCount(totalCount);
        commentLevelCountVO.setGoodCount(goodCount);
        commentLevelCountVO.setNormalCount(normalCount);
        commentLevelCountVO.setBadCount(badCount);
        return commentLevelCountVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCount(String itemId,Integer level) {
        ItemsComments condition=new ItemsComments();
        condition.setItemId(itemId);
        if (level!=null) {
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId,
                                                  Integer level,
                                                  Integer page,
                                                  Integer pageSize) {
        Map<String,Object> map=new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        //分页插件的使用
        //1、在查询之前使用分页插件，原理：统一拦截sql，拼接一些sql片段，为预设的sql提供分页功能
        // page: 第几页；pageSize：每页显示条数
        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> itemCommentVOList = itemsMapperCustom.queryItemComments(map); //itemCommentVOList为分页后的数据
        //商品评价时昵称脱敏
        for (ItemCommentVO vo : itemCommentVOList) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }
        //2、分页数据封装到PagedGridResult传给前端
        return setterPagedGrid(itemCommentVOList, page);
    }

    private PagedGridResult setterPagedGrid(List<?> list,Integer page) {
        PageInfo<?> pageList=new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String,Object> map=new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        //分页
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> searchItemsVOList = itemsMapperCustom.searchItems(map);
        return setterPagedGrid(searchItemsVOList, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String,Object> map=new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        //分页
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> searchItemsVOList = itemsMapperCustom.searchItemsByThirdCat(map);
        return setterPagedGrid(searchItemsVOList, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartItemVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split(",");
        return itemsMapperCustom.queryItemsBySpecIds(Arrays.asList(ids)); //Arrays.asList()将string[] 转化为 List<String>
//        List<String> list=new ArrayList<>();
//        Collections.addAll(list, ids); //Collections.addAll()将String[] 添加到 List<String>
//        return itemsMapperCustom.queryItemsBySpecIds(list);
    }
}
