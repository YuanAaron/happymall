package cn.coderap.controller.center;

import cn.coderap.config.center.FileUpload;
import cn.coderap.controller.BaseController;
import cn.coderap.pojo.Users;
import cn.coderap.pojo.bo.center.CenterUserBO;
import cn.coderap.pojo.vo.UsersVO;
import cn.coderap.service.center.CenterUserService;
import cn.coderap.utils.CookieUtils;
import cn.coderap.utils.DateUtil;
import cn.coderap.utils.JSONResult;
import cn.coderap.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw
 * 2021/1/25
 */
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("/userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @ApiOperation(value = "修改用户头像",notes = "修改用户头像",httpMethod = "POST")
    @PostMapping("/uploadFace")
    public JSONResult uploadFace(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 定义头像保存的地址
        //String fileSpace = USER_FACE_IMAGE_LOCATION;
        String fileSpace = fileUpload.getUserFaceImageLocation();
        // 在路径上增加userId, 用于区分不同用户的上传
        String uploadPathPrefix = File.separator + userId;

        //文件上传
        if (file != null) {
            FileOutputStream fileOutputStream = null;
            try {
                //获取上传文件的文件名
                String filename = file.getOriginalFilename();
                //文件重命名，然后保存到指定目录
                if (StringUtils.isNotBlank(filename)) {
                    String[] fileNameArr = filename.split("\\.");
                    //获取文件的后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];
                    //只在前端限定图片格式为png、jpg、jpeg，而后端没有进行格式校验，导致存在后门。黑客可能上传.sh .php文件，造成风险。
                    if(!"png".equalsIgnoreCase(suffix) &&
                            !"jpg".equalsIgnoreCase(suffix) &&
                            !"jpeg".equalsIgnoreCase(suffix)) {
                        return JSONResult.errorMsg("图片格式不正确");
                    }

                    //重组文件名（这里是覆盖式上传，如果使用增量式，只需要在该文件名上拼接当前时间即可）
                    String newFileName = "face-" + userId + "." + suffix;

                    //上传文件最终保存的位置
                    String uploadPath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                    //提供给web服务访问的地址
                    uploadPathPrefix = uploadPathPrefix + "/" + newFileName;

                    File outFile = new File(uploadPath);
                    //如果文件目录不存在，创建文件夹（可能多级）
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }

                    //保存文件到指定目录
                    InputStream inputStream = file.getInputStream();
                    fileOutputStream = new FileOutputStream(outFile);
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JSONResult.errorMsg("文件不能为空!");
        }

        //获取图片服务地址
        String imageServerUrl = fileUpload.getImageServerUrl();
        //由于浏览器可能存在缓存，所以这里需要加上时间戳以保证更新后的图片可以及时刷新
        String faceUrl = imageServerUrl + uploadPathPrefix + "?t" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);
        //更新用户头像（地址）到数据库，如http://127.0.0.1:8088/images/1908017YR51G1XWH/face-1908017YR51G1XWH.jpg
        Users userRes = centerUserService.updateUserFace(userId, faceUrl);
        //更新Users后，覆盖cookie
//        userRes = setNullProperty(userRes);

        //在分布式会话中，增加令牌token，整合进redis
        UsersVO usersVO = convertToUsersVO(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO),true);

        return JSONResult.ok();
    }

    @ApiOperation(value = "更新用户信息",notes = "更新用户信息",httpMethod = "POST")
    @PostMapping("/update")
    public JSONResult update(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "centerUserBO",value = "用户传入的数据封装在此entity中",required = true)
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response) {

        //判断result中是否包含错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return JSONResult.ok(errorMap);
        }

        Users userRes = centerUserService.updateUserInfo(userId, centerUserBO);
        //更新Users后，覆盖cookie
//        userRes = setNullProperty(userRes);

        //在分布式会话中，增加令牌token，整合进redis
        UsersVO usersVO = convertToUsersVO(userRes);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO),true);

        return JSONResult.ok();
    }

    //处理错误信息
    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> fieldErrorList = result.getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            //发生验证错误的属性
            String errorField = fieldError.getField();
            //验证错误信息
            String errorMessage = fieldError.getDefaultMessage();
            map.put(errorField, errorMessage);
        }
        return map;
    }

    private Users setNullProperty(Users userRes) {
        userRes.setPassword(null);
        userRes.setRealname(null);
        userRes.setMobile(null);
        userRes.setEmail(null);
        userRes.setSex(null);
        userRes.setBirthday(null);
        userRes.setCreatedTime(null);
        userRes.setUpdatedTime(null);
        return userRes;
    }
}
