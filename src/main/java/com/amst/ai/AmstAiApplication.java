package com.amst.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.amst.ai.*.mapper")
@SpringBootApplication
public class AmstAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmstAiApplication.class, args);
    }

}
