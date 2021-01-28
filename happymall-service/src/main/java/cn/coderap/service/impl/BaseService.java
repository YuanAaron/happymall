package cn.coderap.service.impl;

import cn.coderap.utils.PagedGridResult;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by yw
 * 2021/1/28
 */
public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList=new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
