package com.kakaopay.homework;

import com.kakaopay.homework.configuration.FileUploadConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(
        {
                FileUploadConfiguration.class
        }
)
@SpringBootApplication
public class InternetBankingStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternetBankingStatisticsApplication.class, args);
    }

}
