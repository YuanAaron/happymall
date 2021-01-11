package cn.coderap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
//扫描通用mapper所在的包
@MapperScan(basePackages = "cn.coderap.mapper")
//除了默认扫描cn.coderap外，增加扫描全局唯一id相关组件包
@ComponentScan(basePackages = {"cn.coderap","org.n3r.idworker"})
//开启定时任务
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
