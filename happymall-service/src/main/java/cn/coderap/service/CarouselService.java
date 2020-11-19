package cn.coderap.service;

import cn.coderap.pojo.Carousel;

import java.util.List;

public interface CarouselService {

    /**
     * 获取首页所有轮播图
     * @param isShow
     * @return
     */
    public List<Carousel> queryAll(Integer isShow);
}
