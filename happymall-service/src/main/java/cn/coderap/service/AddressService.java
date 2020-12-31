package cn.coderap.service;

import cn.coderap.pojo.UserAddress;
import cn.coderap.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {

    /**
     * 获取所有收货地址
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 新增用户收货地址
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改用户收货地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 删除用户收货地址
     */
    public void deleteUserAddress(String userId,String addressId);

    /**
     * 设置默认用户收货地址
     */
    public void updateUserAddressToBeDefault(String userId,String addressId);

    /**
     * 根据userId和addressId查询用户地址对象
     */
    public UserAddress queryUserAddress(String userId, String addressId);
}
