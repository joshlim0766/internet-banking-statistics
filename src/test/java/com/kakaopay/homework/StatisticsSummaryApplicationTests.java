package com.kakaopay.homework;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.service.InternetBankingStatisticsService;
import com.kakaopay.homework.internetbanking.utility.csv.RawData;
import com.kakaopay.homework.internetbanking.utility.csv.RawDataParser;
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
public class InternetBankingStatisticsApplicationTests {

    @Autowired
    private InternetBankingStatisticsService internetBankingStatisticsService;

    private RawDataParser rawDataParser = new RawDataParser();
    private RawData rawData = null;

    @Before
    public void init () {
        ClassPathResource resource = new ClassPathResource("data.csv");

        try {
            File dataFile = resource.getFile();
            Assert.assertTrue(dataFile.exists());

            rawData = rawDataParser.parse(dataFile.getAbsolutePath());

            Assert.assertNotNull(rawData);

            Assert.assertTrue(rawData.getHeader().size() == 7);
            Assert.assertTrue(rawData.getRows().size() == 8);
        }
        catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testGetDevices () {
        DeviceStatisticsResponse response = internetBankingStatisticsService.getDevices();
        Assert.assertNotNull(response);

        List<DeviceStatisticsDTO> list = response.getDeviceStatisticsList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 5);

        Assert.assertTrue(list.stream()
                .filter(deviceStatisticsDTO -> rawData.getHeader().contains(deviceStatisticsDTO.getDeviceName()))
                .count() == rawData.getHeader().size() - 2);

        Assert.assertTrue(list.stream()
                .filter(deviceStatisticsDTO -> deviceStatisticsDTO.getDeviceId() != null)
                .count() == list.size());

        Assert.assertTrue(list.stream()
                .map(deviceStatisticsDTO -> deviceStatisticsDTO.getDeviceId())
                .collect(Collectors.toSet()).stream()
                .count() == list.size());
    }

    @Test
    public void testGetYearlyDeviceStatistics () {
        DeviceStatisticsResponse response = internetBankingStatisticsService.getYearlyDeviceStatistics();

        Assert.assertNotNull(response);

        List<DeviceStatisticsDTO> list = response.getDeviceStatisticsList();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 8);
    }
}
