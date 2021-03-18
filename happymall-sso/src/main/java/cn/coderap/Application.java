package cn.coderap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//扫描通用mapper所在的包
@MapperScan(basePackages = "cn.coderap.mapper")
//除了默认扫描cn.coderap外，增加扫描全局唯一id相关组件包
@ComponentScan(basePackages = {"cn.coderap","org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
