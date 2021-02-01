package cn.coderap;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Created by yw
 * 2021/2/1
 */
//打包：第四步，添加war的启动类
public class WarStarterApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //指定Application启动类
        return builder.sources(Application.class);
    }
}
