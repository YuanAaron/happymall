package cn.coderap.controller;

import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.pojo.Carousel;
import cn.coderap.service.CarouselService;
import cn.coderap.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel() {
        List<Carousel> carouselList = carouselService.queryAll(YesOrNoEnum.YES.type);
        return JSONResult.ok(carouselList);
    }

    /**
     * 首页分类展示需求分析：
     * 1、刷新首页时只获取一级大分类，并渲染展示在首页；
     * 2、当鼠标移动到一级大分类上，才加载其子分类的内容（懒加载）；如果已经存在子分类（鼠标已经到该分类过），则不再加载其子分类内容。
     */
}