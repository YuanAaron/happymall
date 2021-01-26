package cn.coderap.Exception;

import cn.coderap.utils.JSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Created by yw
 * 2021/1/26
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    //MaxUploadSizeExceededException来自控制台报告的异常
    //上传文件超过500k时，捕获MaxUploadSizeExceededException异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JSONResult handleMaxUploadFile(MaxUploadSizeExceededException ex) {
        return JSONResult.errorMsg("上传文件大小不能超过500k,请压缩图片或降低图片质量后再上传!");
    }
}
