package cn.coderap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域
 * Created by yw
 * 2020/11/16
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        //1、添加cors配置信息
        CorsConfiguration config=new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080"); //在这里localhost和127.0.0.1不同，两个最好都添加上
        config.addAllowedOrigin("http://127.0.0.1:8080");
        config.addAllowedOrigin("http://47.93.190.199:8080"); //tomcat启动前端时使用 如果使用域名，happymall-web和happymall-center都要添加
        config.addAllowedOrigin("http://47.93.190.199"); //nginx启动前端时使用 如果使用域名，happymall-web和happymall-center都要添加
        config.addAllowedOrigin("http://web.coderap.cn:8080");
        config.addAllowedOrigin("http://center.coderap.cn:8080");
        config.addAllowedOrigin("http://web.coderap.cn");
        config.addAllowedOrigin("http://center.coderap.cn");
        //设置是否发送cookie信息
        config.setAllowCredentials(true);
        //设置允许请求的方式，比如GET、POST等
        config.addAllowedMethod("*");
        //设置允许的header
        config.addAllowedHeader("*");

        //2、为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource=new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config); //适用于所有的路由

        //3、返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}
