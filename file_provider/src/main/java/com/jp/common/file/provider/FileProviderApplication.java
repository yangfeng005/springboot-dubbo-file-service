package com.jp.common.file.provider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@ComponentScan({"com.jp.framework","com.jp.common.file.provider"})
@MapperScan("com.jp.common.file.provider.dao")
@ImportResource({"classpath:conf/*.xml" })
public class FileProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProviderApplication.class, args);
    }

}

