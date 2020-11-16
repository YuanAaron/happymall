package cn.coderap.controller;

import cn.coderap.service.StuServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by yw
 * 2020/11/6
 */
@ApiIgnore
@RestController
public class StuController {

    @Autowired
    private StuServcie stuServcie;

    @GetMapping("/getStu")
    public Object getStu(int id) {
        return stuServcie.getStuInfo(id);
    }

    //不满足幂等性
    @PostMapping("/saveStu")
    public Object saveStu() {
        stuServcie.saveStu();
        return "OK";
    }

    //满足幂等性
    @PostMapping("/updateStu")
    public Object updateStu(int id) {
        stuServcie.updateStu(id);
        return "OK";
    }

    //满足幂等性
    @PostMapping("/deleteStu")
    public Object deleteStu(int id) {
        stuServcie.deleteStu(id);
        return "OK";
    }
}
