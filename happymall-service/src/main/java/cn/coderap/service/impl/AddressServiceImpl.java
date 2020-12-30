package cn.coderap.service.impl;

import cn.coderap.enums.YesOrNoEnum;
import cn.coderap.mapper.UserAddressMapper;
import cn.coderap.pojo.UserAddress;
import cn.coderap.pojo.bo.AddressBO;
import cn.coderap.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by yw
 * 2020/12/29
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress ua = new UserAddress();
        ua.setUserId(userId);
        return userAddressMapper.select(ua);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        //1、判断当前用户是否已经有收货地址，如果没有，设为默认收货地址
        Integer isDefault = YesOrNoEnum.NO.type;
        List<UserAddress> userAddressList = queryAll(addressBO.getUserId());
        if (userAddressList == null || userAddressList.isEmpty() || userAddressList.size() == 0) {
            isDefault = YesOrNoEnum.YES.type;
        }
        //2、保存新增收货地址
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        String id = sid.nextShort();
        userAddress.setId(id);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());
        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(addressBO.getAddressId());
        userAddress.setUpdatedTime(new Date());
        //updateByPrimaryKeySelective()不会把null值插入数据库，避免覆盖之前有值的，因为sql会对每个属性进行是否为null的判断
        //但是updateByPrimaryKey()就会根据传入的对象，全部取值插入数据库，会存在覆盖数据的问题，即sql没有对属性进行是否为null的判断
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);
        userAddressMapper.delete(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        //1、查找当前默认收货地址，设置为非默认
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setIsDefault(YesOrNoEnum.YES.type);
        //防止数据混乱，出现多个默认地址
        List<UserAddress> list = userAddressMapper.select(userAddress);
        list.forEach(item -> {
            item.setIsDefault(YesOrNoEnum.NO.type);
            userAddressMapper.updateByPrimaryKeySelective(item);
        });
        //2、将addressId修改为默认地址
        UserAddress defaultUserAddress = new UserAddress();
        defaultUserAddress.setUserId(userId);
        defaultUserAddress.setId(addressId);
        defaultUserAddress.setIsDefault(YesOrNoEnum.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultUserAddress);
    }
}
