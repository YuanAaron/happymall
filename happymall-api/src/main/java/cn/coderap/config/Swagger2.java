package cn.coderap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by yw
 * 2020/11/16
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

//    127.0.0.1:8088/swagger-ui.html  //原路径
//    127.0.0.1:8088/doc.html  //新路径

    //swagger2核心配置
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2) //指定api类型为swagger2
                .apiInfo(apiInfo())  //用于定义api文档汇总信息
                .select().apis(RequestHandlerSelectors.basePackage("cn.coderap.controller"))  //指定controller包
                .paths(PathSelectors.any())  //所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("电商平台接口api")  //文档页标题
                .contact(new Contact("coderap", "https://www.coderap.cn", "1500438364@qq.com"))  //联系人信息
                .description("专为我的电商平台提供的api文档")  //详细信息
                .version("1.0.1")  //文档版本号
                .termsOfServiceUrl("https://www.coderap.cn/happymall")  //电商网站地址
                .build();
    }
}
