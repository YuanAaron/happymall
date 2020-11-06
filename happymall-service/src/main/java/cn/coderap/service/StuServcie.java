package cn.coderap.service;

import cn.coderap.pojo.Stu;

/**
 * Created by yw
 * 2020/11/6
 */
public interface StuServcie {

    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);
}
