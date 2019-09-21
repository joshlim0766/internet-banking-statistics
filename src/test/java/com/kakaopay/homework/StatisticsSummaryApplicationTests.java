package com.kakaopay.homework;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceResponse;
import com.kakaopay.homework.internetbanking.controller.dto.StatisticsDTO;
import com.kakaopay.homework.internetbanking.controller.dto.StatisticsResponse;
import com.kakaopay.homework.internetbanking.service.DeviceService;
import com.kakaopay.homework.internetbanking.service.StatisticsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsSummaryApplicationTests {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private DeviceService deviceService;

    @Before
    public void init () {
        ClassPathResource resource = new ClassPathResource("data.csv");

        try {
            File dataFile = resource.getFile();
            Assert.assertTrue(dataFile.exists());

            statisticsService.loadData();
        }
        catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetDevices () {
        DeviceResponse response = deviceService.getDevices();
        Assert.assertNotNull(response);

        List<DeviceDTO> list = response.getDeviceList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 5);

        Assert.assertTrue(list.stream()
                .filter(deviceDTO -> deviceDTO.getDeviceId() != null)
                .count() == list.size());

        Assert.assertTrue(list.stream()
                .map(deviceDTO -> deviceDTO.getDeviceId())
                .collect(Collectors.toSet()).stream()
                .count() == list.size());
    }

    @Test
    public void testGetYearlyDeviceStatistics () {
        StatisticsResponse response = statisticsService.getYearlyDeviceStatistics();

        Assert.assertNotNull(response);

        List<StatisticsDTO> list = response.getDeviceStatisticsList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 8);
    }
}
