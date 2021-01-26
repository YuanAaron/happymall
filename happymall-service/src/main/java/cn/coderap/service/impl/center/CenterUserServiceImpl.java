package cn.coderap.service.impl.center;

import cn.coderap.mapper.UsersMapper;
import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.center.CenterUserBO;
import cn.coderap.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by yw
 * 2021/1/25
 */
@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        user.setPassword(null);
        return usersMapper.selectByPrimaryKey(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        Users user = new Users();
        BeanUtils.copyProperties(centerUserBO, user);
        user.setId(userId);
        user.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(user);
        //查询最新user用以更新cookie
        return queryUserInfo(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String faceUrl) {
        Users user = new Users();
        user.setId(userId);
        user.setFace(faceUrl);
        user.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserInfo(userId);
    }
}
