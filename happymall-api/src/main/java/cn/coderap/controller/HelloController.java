package cn.coderap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class HelloController {

    private final static Logger logger= LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public String hello() {
        logger.debug("debug: hello");
        logger.info("info: hello");
        logger.warn("warn: hello");
        logger.error("error: hello");
        return "hello world";
    }
}
