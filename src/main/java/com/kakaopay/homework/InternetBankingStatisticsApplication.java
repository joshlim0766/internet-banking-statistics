package com.kakaopay.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class InternetBankingStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternetBankingStatisticsApplication.class, args);
    }

}
