package cn.coderap.controller;

import cn.coderap.pojo.*;
import cn.coderap.pojo.vo.CommentLevelCountVO;
import cn.coderap.pojo.vo.ItemInfoVO;
import cn.coderap.service.ItemService;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
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
public class ItemsController extends BaseController{

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

    @ApiOperation(value = "查询商品评价内容",notes = "查询商品评价内容",httpMethod = "GET")
    @GetMapping("/comments")
    public JSONResult comments(
            @ApiParam(name = "itemId",value ="商品id",required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value ="评价等级",required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page",value ="当前页数",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value ="每页显示条数",required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return JSONResult.errorMsg("商品不存在"); //null
        }

        if (page==null) {
            page=1;
        }

        if (pageSize==null) {
            pageSize=COMMENT_PAGE_SIZE;
        }
        PagedGridResult grid = itemService.queryPagedComments(itemId, level, page, pageSize);
        return JSONResult.ok(grid);
    }

    @ApiOperation(value = "根据关键字搜索商品列表",notes = "根据关键字搜索商品列表",httpMethod = "GET")
    @GetMapping("/search")
    public JSONResult search(
            @ApiParam(name = "keywords",value ="关键字",required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort",value ="排序",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value ="当前页数",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value ="每页显示条数",required = false)
            @RequestParam Integer pageSize) {

        //有该段代码：当没有关键词时，不去搜索；
        //没有该段代码：如果前端没有做空校验，当没有关键词时，搜索出所有内容
        if (StringUtils.isBlank(keywords)) {
            return JSONResult.errorMsg("null");
        }

        if (page==null) {
            page=1;
        }

        if (pageSize==null) {
            pageSize=PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(keywords, sort, page, pageSize);
        return JSONResult.ok(grid);
    }

    @ApiOperation(value = "根据三级小分类id搜索商品列表",notes = "根据三级小分类id搜索商品列表",httpMethod = "GET")
    @GetMapping("/catItems")
    public JSONResult catItems(
            @ApiParam(name = "catId",value ="三级小分类id",required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort",value ="排序",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value ="当前页数",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value ="每页显示条数",required = false)
            @RequestParam Integer pageSize) {

        if (catId==null) {
            return JSONResult.errorMsg("null");
        }

        if (page==null) {
            page=1;
        }

        if (pageSize==null) {
            pageSize=PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(catId, sort, page, pageSize);
        return JSONResult.ok(grid);
    }

    //用于用户长时间未登录网站时，刷新购物车中的数据
    @ApiOperation(value = "根据三级小分类id搜索商品列表",notes = "根据三级小分类id搜索商品列表",httpMethod = "GET")
    @GetMapping("/catItems")
    public JSONResult catItems(
            @ApiParam(name = "catId",value ="三级小分类id",required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort",value ="排序",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value ="当前页数",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value ="每页显示条数",required = false)
            @RequestParam Integer pageSize) {

        if (catId==null) {
            return JSONResult.errorMsg("null");
        }

        if (page==null) {
            page=1;
        }

        if (pageSize==null) {
            pageSize=PAGE_SIZE;
        }
        PagedGridResult grid = itemService.searchItems(catId, sort, page, pageSize);
        return JSONResult.ok(grid);
    }

}
