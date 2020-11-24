package cn.coderap.controller;

import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.pojo.*;
import cn.coderap.pojo.vo.CategoryVO;
import cn.coderap.pojo.vo.CommentLevelCountVO;
import cn.coderap.pojo.vo.ItemInfoVO;
import cn.coderap.pojo.vo.NewItemsVO;
import cn.coderap.service.ItemService;
import cn.coderap.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yw
 * 2020/11/23
 */
@Api(value = "商品",tags = {"用于商品信息展示的相关接口"})
@RestController
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情",notes = "查询商品详情",httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JSONResult info(@ApiParam(name = "itemId",value ="商品id",required = true)
                                   @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg("商品不存在"); //null
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.getItemsImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.getItemSpecList(itemId);
        ItemsParam itemParams = itemService.getItemParam(itemId);

        ItemInfoVO itemInfoVO=new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemParams);
        return JSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询评价等级数量",notes = "查询评价等级数量",httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JSONResult commentLevel(@ApiParam(name = "itemId",value ="商品id",required = true)
                           @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg("商品不存在"); //null
        }
        CommentLevelCountVO commentLevelCountVO = itemService.queryCommentCount(itemId);
        return JSONResult.ok(commentLevelCountVO);
    }

}
