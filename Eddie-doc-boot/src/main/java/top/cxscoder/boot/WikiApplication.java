package top.cxscoder.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Edward
 * @date 2023-11-30 14:01
 * @copyright Copyright (c) 2023 Edward
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "top.cxscoder.system",
        "top.cxscoder.boot",
        "top.cxscoder.common",
        "top.cxscoder.wiki",

})
@MapperScan({"top.cxscoder.system.mapper","top.cxscoder.wiki.repository.*"})
public class WikiApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(WikiApplication.class, args);
        run.getBean("userController");
    }
}
