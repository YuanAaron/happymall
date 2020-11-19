package cn.coderap.service.impl;

import cn.coderap.mapper.CarouselMapper;
import cn.coderap.pojo.Carousel;
import cn.coderap.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by yw
 * 2020/11/19
 */
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<Carousel> queryAll(Integer isShow) {
        Example carouselExample=new Example(Carousel.class);
        carouselExample.orderBy("sort").desc();  //倒序
        Example.Criteria carouselCriteria = carouselExample.createCriteria();
        carouselCriteria.andEqualTo("isShow",isShow);
        List<Carousel> list = carouselMapper.selectByExample(carouselExample);
        return list;
    }
}
