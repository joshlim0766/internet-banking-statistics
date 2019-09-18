package com.kakaopay.homework;

import com.kakaopay.homework.internetbanking.service.InternetBankingStatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InternetBankingStatisticsApplicationTests {

    @Autowired
    private InternetBankingStatisticsService internetBankingStatisticsService;
    @Test
    public void contextLoads() {
        ClassPathResource resource = new ClassPathResource("data.csv");

        try {
            Assert.assertTrue(resource.getFile().exists());
        }
        catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
