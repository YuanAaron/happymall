package cn.coderap.controller;

import cn.coderap.pojo.UserAddress;
import cn.coderap.pojo.bo.AddressBO;
import cn.coderap.pojo.vo.NewItemsVO;
import cn.coderap.service.AddressService;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.xml.soap.Detail;
import java.util.List;

@Api(value = "地址相关",tags = {"地址相关的接口"})
@RestController
@RequestMapping("/address")
public class AddressController {

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1、查询用户的所有收货地址列表
     * 2、新增收获地址
     * 3、删除收获地址
     * 4、修改收获地址
     * 5、设置默认地址
     */

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "根据用户id查询收获地址列表",notes = "根据用户id查询收获地址列表",httpMethod = "POST")
    @PostMapping("/list")
    public JSONResult list(@ApiParam(name = "userId",value ="用户id",required = true)
                                  @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }
        List<UserAddress> userAddressList = addressService.queryAll(userId);
        return JSONResult.ok(userAddressList);
    }

    @ApiOperation(value = "新增用户收货地址",notes = "新增用户收货地址",httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(@ApiParam(name = "addressBO",value ="新增用户收货地址BO",required = true)
                           @RequestBody AddressBO addressBO) {
        JSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.addNewUserAddress(addressBO);
        return JSONResult.ok();
    }

    @ApiOperation(value = "修改用户收货地址",notes = "修改用户收货地址",httpMethod = "POST")
    @PostMapping("/update")
    public JSONResult update(@ApiParam(name = "addressBO",value ="修改用户收货地址BO",required = true)
                          @RequestBody AddressBO addressBO) {
        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return JSONResult.errorMsg("修改收货地址错误：addressId不能为空");
        }
        JSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.updateUserAddress(addressBO);
        return JSONResult.ok();
    }

    private JSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return JSONResult.errorMsg("收货人姓名不能为空");
        }
        if (receiver.length()>12) {
            return JSONResult.errorMsg("收货人姓名不能太长");
        }
        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return JSONResult.errorMsg("手机号不能为空");
        }
        if (mobile.length() != 11) {
            return JSONResult.errorMsg("手机号长度为11位");
        }

        boolean isMobileOK = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOK) {
            return JSONResult.errorMsg("手机号格式不正确");
        }

        if (StringUtils.isBlank(addressBO.getProvince()) ||
                StringUtils.isBlank(addressBO.getCity()) ||
                StringUtils.isBlank(addressBO.getDistrict()) ||
                StringUtils.isBlank(addressBO.getDetail())) {
            return JSONResult.errorMsg("收货地址信息不能为空");
        }
        return JSONResult.ok();
    }

    @ApiOperation(value = "删除用户收货地址",notes = "删除用户收货地址",httpMethod = "POST")
    @PostMapping("/delete")
    public JSONResult delete(
            @ApiParam(name = "userId",value ="用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId",value ="用户收货地址id",required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return JSONResult.errorMsg("");
        }
        addressService.deleteUserAddress(userId,addressId);
        return JSONResult.ok();
    }

    @ApiOperation(value = "设置默认收货地址",notes = "设置默认收货地址",httpMethod = "POST")
    @PostMapping("/setDefault")
    public JSONResult setDefault(
            @ApiParam(name = "userId",value ="用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId",value ="用户收货地址id",required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return JSONResult.errorMsg("");
        }
        addressService.updateUserAddressToBeDefault(userId,addressId);
        return JSONResult.ok();
    }

}
