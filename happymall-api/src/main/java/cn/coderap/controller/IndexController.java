package cn.coderap.controller;

import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.pojo.Carousel;
import cn.coderap.pojo.Category;
import cn.coderap.pojo.vo.CategoryVO;
import cn.coderap.pojo.vo.NewItemsVO;
import cn.coderap.service.CarouselService;
import cn.coderap.service.CategoryService;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import cn.coderap.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw
 * 2020/11/19
 */
@Api(value = "首页",tags = {"用于首页展示的相关接口"})
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel() {
        /**
         * 轮播图（广告位）发生变更
         * 方案一、后台运营系统手动修改轮播图后，手动删除缓存(carousel键)
         * 方案二、定时重置，比如每天凌晨三点清除缓存（carousel键），然后再把新的轮播图加入。
         * 方案三、每个轮播图都可能是一个广告，每个广告都会有一个过期时间，过期了缓存（carousel键）自动删除，然后再把新的轮播图（广告）加入进来
         */

        //使用redis优化轮播图查询
        String carousel = redisOperator.get("carousel");
        List<Carousel> carouselList = new ArrayList<>();
        if (StringUtils.isBlank(carousel)) {
            carouselList = carouselService.queryAll(YesOrNoEnum.YES.type);
            redisOperator.set("carousel", JsonUtils.objectToJson(carouselList));
        } else {
            carouselList = JsonUtils.jsonToList(carousel, Carousel.class);
        }
        return JSONResult.ok(carouselList);
    }

    /**
     * 首页分类展示需求分析：
     * 1、刷新首页时只获取一级大分类，并渲染展示在首页；
     * 2、当鼠标移动到一级大分类上，才加载其子分类的内容（懒加载）；如果已经存在子分类（鼠标已经到该分类过），则不再加载其子分类内容。
     */

    @ApiOperation(value = "获取商品分类（一级分类）",notes = "获取商品分类（一级分类）",httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats() {
        //使用redis优化一级分类
        String cats = redisOperator.get("cats");
        List<Category> categoryList = new ArrayList<>();
        if (StringUtils.isBlank(cats)) {
            categoryList = categoryService.queryAllRootCategory();
            redisOperator.set("cats", JsonUtils.objectToJson(categoryList));
        } else {
            categoryList = JsonUtils.jsonToList(cats, Category.class);
        }
        return JSONResult.ok(categoryList);
    }

    @ApiOperation(value = "获取商品子分类（二级分类及其三级小分类）",notes = "获取商品子分类（二级分类及其三级小分类）",httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(@ApiParam(name = "rootCatId",value ="一级大分类id",required = true)
                                 @PathVariable("rootCatId") Integer rootCategoryId) {
        if (rootCategoryId==null) {
            return JSONResult.errorMsg("分类不存在");
        }
        //使用redis优化二级分类及其三级小分类
        String subCat = redisOperator.get("subCat:" + rootCategoryId);
        List<CategoryVO> categoryVOList = new ArrayList<>();
        if (StringUtils.isBlank(subCat)) {
            categoryVOList = categoryService.getSubCategoryList(rootCategoryId);
            redisOperator.set("subCat:" + rootCategoryId, JsonUtils.objectToJson(categoryVOList));
        } else {
            categoryVOList = JsonUtils.jsonToList(subCat, CategoryVO.class);
        }
        return JSONResult.ok(categoryVOList);
    }

    @ApiOperation(value = "获取每个一级大分类下的最新6条商品数据",notes = "获取每个一级大分类下的最新6条商品数据",httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(@ApiParam(name = "rootCatId",value ="一级大分类id",required = true)
                             @PathVariable("rootCatId") Integer rootCategoryId) {
        if (rootCategoryId==null) {
            return JSONResult.errorMsg("分类不存在");
        }
        List<NewItemsVO> newItemsVOList = categoryService.lazyGetSixNewItems(rootCategoryId);
        return JSONResult.ok(newItemsVOList);
    }

}
