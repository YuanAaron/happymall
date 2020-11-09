package cn.coderap.service.impl;

import cn.coderap.mapper.UsersMapper;
import cn.coderap.pojo.Users;
import cn.coderap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean queryUsernameIsExist(String username) {
        //Users对象映射为Example
        Example userExample=new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users res = usersMapper.selectOneByExample(userExample);
        return res==null?false:true;
    }
}
