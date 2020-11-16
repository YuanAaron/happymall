package cn.coderap.service;

import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.UserBO;

public interface UserService {

    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 注册用户
     */
    public Users createUser(UserBO userBO);
}
