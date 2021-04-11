package com.example;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年04月08日 23:27
 */
import com.example.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.example.mapper")
public class StockApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(StockApplication.class, args);
        SpringContextUtil.setApplicationContext(run);
    }
}
