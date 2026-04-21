package com.cold73.campuserrand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cold73.campuserrand.mapper")
public class CampusErrandApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusErrandApplication.class, args);
    }

}
