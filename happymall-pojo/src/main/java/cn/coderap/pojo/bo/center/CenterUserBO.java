package cn.coderap.pojo.bo.center;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Created by yw
 * 2021/1/25
 *
 * Bean Validation 中内置的 constraint：
 * @Null 被注释的元素必须为 null
 * @NotNull 被注释的元素必须不为 null
 * @AssertTrue 被注释的元素必须为 true
 * @AssertFalse 被注释的元素必须为 false
 * @Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
 * @Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
 * @DecimalMin(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
 * @DecimalMax(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
 * @Size(max=, min=) 被注释的元素的大小必须在指定的范围内
 * @Digits (integer, fraction) 被注释的元素必须是一个数字，其值必须在可接受的范围内
 * @Past 被注释的元素必须是一个过去的日期
 * @Future 被注释的元素必须是一个将来的日期
 * @Pattern(regex=,flag=) 被注释的元素必须符合指定的正则表达式
 * Hibernate Validator 附加的 constraint
 * @NotBlank(message =) 验证字符串非null，且长度必须大于0
 * @Email 被注释的元素必须是电子邮箱地址
 * @Length(min=,max=) 被注释的字符串的大小必须在指定的范围内
 * @NotEmpty 被注释的字符串必须非空
 * @Range(min=,max=,message=) 被注释的元素必须在合适的范围内
 */
@ApiModel(value="用户对象", description="从客户端，由用户传入的数据封装在此entity中")
public class CenterUserBO {

    @ApiModelProperty(value = "用户名", name = "username", example = "json", required = false)
    private String username;

    //密码相关暂时用不到

    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = false)
    private String password;

    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = false)
    private String confirmPassword;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 12, message = "用户昵称不能超过12位")
    @ApiModelProperty(value = "用户昵称", name = "nickname", example = "杰森", required = false)
    private String nickname;

    @Length(max = 12, message = "用户真实姓名不能超过12位")
    @ApiModelProperty(value = "真实姓名", name = "realname", example = "杰森", required = false)
    private String realname;

    @Pattern(regexp = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号", name = "mobile", example = "13999999999", required = false)
    private String mobile;

    //@Pattern和@Email选其一即可
    @Pattern(regexp = "^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$", message = "邮箱格式不正确")
    //@Email
    @ApiModelProperty(value = "邮箱地址", name = "email", example = "imooc@imooc.com", required = false)
    private String email;

    //测试发现：0.6会被转成0，1.6会被转成1，2.6会被转成2
    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 2, message = "性别选择不正确")
    @ApiModelProperty(value = "性别", name = "sex", example = "0:女 1:男 2:保密", required = false)
    private Integer sex;

    @ApiModelProperty(value = "生日", name = "birthday", example = "1900-01-01", required = false)
    private Date birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
