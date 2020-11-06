package cn.coderap.service.impl;

import cn.coderap.mapper.StuMapper;
import cn.coderap.pojo.Stu;
import cn.coderap.service.StuServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yw
 * 2020/11/6
 */
@Service
public class StuServiceImpl implements StuServcie {

    @Autowired
    private StuMapper stuMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveStu() {
        Stu stu=new Stu();
        stu.setName("赵六");
        stu.setAge(26);
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(int id) {
        Stu stu=new Stu();
        stu.setId(id);
        stu.setName("王五");
        stu.setAge(26);
        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }
}
