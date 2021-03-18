package cn.coderap.config;

import cn.coderap.controller.interceptor.UserTokenInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by yw
 * 2021/1/5
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //1、发布/upload/happymall下的所有资源到网络，以供浏览器访问。这样可以使用
        //http://127.0.0.1:8088/images/1908017YR51G1XWH(userId)/face-1908017YR51G1XWH.jpg访问/upload/happymall/下的静态资源
        //2、覆盖了原来的addResourceHandlers方法，如果不为swagger2添加映射，将无法通过http://127.0.0.1:8088/doc.html#/访问/META-INF/resources/下的资源
        registry.addResourceHandler("/**") //对应路径下的所有资源
                .addResourceLocations("file:/upload/happymall/") //为本地静态资源（头像）添加映射
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/hello");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
