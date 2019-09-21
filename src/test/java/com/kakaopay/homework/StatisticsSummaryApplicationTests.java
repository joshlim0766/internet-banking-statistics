package com.kakaopay.homework;

import com.kakaopay.homework.controller.dto.DeviceDTO;
import com.kakaopay.homework.controller.dto.DeviceResponse;
import com.kakaopay.homework.controller.dto.StatisticsDTO;
import com.kakaopay.homework.controller.dto.StatisticsResponse;
import com.kakaopay.homework.repository.DeviceRepository;
import com.kakaopay.homework.service.DeviceService;
import com.kakaopay.homework.service.StatisticsService;
import com.kakaopay.homework.utility.DeviceIdGenerator;
import com.kakaopay.homework.utility.RawStatisticsDataParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsSummaryApplicationTests {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceIdGenerator deviceIdGenerator;

    @Autowired
    private RawStatisticsDataParser statisticsDataParser;

    @Autowired
    private DeviceRepository deviceRepository;

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

    private LinkedHashMap<Short, LinkedHashMap<String, Double>> loadRatePerDevice () {
        ClassPathResource resource = new ClassPathResource("data.csv");

        List<String> deviceNameList = new ArrayList<>();
        LinkedHashMap<Short, LinkedHashMap<String, Double>> rateMap = new LinkedHashMap<>();

        try {
            File dataFile = resource.getFile();
            statisticsDataParser.parse(dataFile.getAbsolutePath(), (isHeader, row) -> {
                if (isHeader) {
                    Stream.of(row).skip(2).forEach(column -> deviceNameList.add(column));
                }
                else {
                    short year = Short.valueOf(row[0]);

                    LinkedHashMap<String, Double> ratePerDevice = new LinkedHashMap<String, Double>();
                    rateMap.put(year, ratePerDevice);
                    IntStream.range(2, row.length).forEach(i -> {
                        ratePerDevice.put(deviceNameList.get(i - 2), row[i].equals("-") ? 0.0d : Double.valueOf(row[i]));
                    });
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }

        Assert.assertTrue(rateMap.size() != 0);

        return rateMap;
    }

    private Map<String, DeviceDTO> loadDevices () {
        Map<String, DeviceDTO> result = new HashMap<>();

        deviceRepository.findAll().stream()
                .map(device -> new DeviceDTO(device.getDeviceId(), device.getDeviceName()))
                .forEach(deviceDTO -> result.put(deviceDTO.getDeviceName(), deviceDTO));

        return result;
    }

    @Test
    public void testDeviceIdGenerator () {
        Set<String> generatedIds = new HashSet<>();

        IntStream.range(0, 100000)
                .forEach(i ->generatedIds.add(deviceIdGenerator.generate("testDevice-" + i)));

        Assert.assertTrue(generatedIds.size() == 100000);

        generatedIds.clear();

        IntStream.range(0, 100)
                .forEach(i -> {
                    try {
                        generatedIds.add(deviceIdGenerator.generate("testDevice"));
                        Thread.sleep(10);
                    }
                    catch (Exception e) {}
                });

        Assert.assertTrue(generatedIds.size() == 100);
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

        LinkedHashMap<Short, LinkedHashMap<String, Double>> rateMap = loadRatePerDevice();
        List<StatisticsDTO> list = response.getDeviceStatisticsList();

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == rateMap.size());

        list.stream().forEach(dto -> {
            Assert.assertTrue(rateMap.containsKey(dto.getYear()));

            LinkedHashMap<String, Double> m = rateMap.get(dto.getYear());

            Assert.assertTrue(m.containsKey(dto.getDeviceName()));

            Assert.assertTrue(dto.getRate() == m.entrySet().stream()
                    .map(entry -> entry.getValue())
                    .sorted(Comparator.reverseOrder())
                    .limit(1)
                    .findFirst().orElse(0.0d).doubleValue());
        });
    }

    @Test
    public void testgetDeviceStatisticsByYear () {
        LinkedHashMap<Short, LinkedHashMap<String, Double>> rateMap = loadRatePerDevice();

        try {
            statisticsService.getDeviceStatisticsByYear((short) -1500);
            statisticsService.getDeviceStatisticsByYear((short) 4000);
        }
        catch (Exception e) {
            Assert.assertTrue(true);
        }

        rateMap.keySet().stream().map(year -> statisticsService.getDeviceStatisticsByYear(year)).forEach(response -> {
            Assert.assertNotNull(response);

            StatisticsDTO dto = response.getStatisticsDTO();

            Assert.assertNotNull(dto);

            Assert.assertTrue(rateMap.containsKey(dto.getYear()));

            LinkedHashMap<String, Double> m = rateMap.get(dto.getYear());

            Assert.assertTrue(m.containsKey(dto.getDeviceName()));

            Assert.assertTrue(dto.getRate() == m.entrySet().stream()
                    .map(entry -> entry.getValue())
                    .sorted(Comparator.reverseOrder())
                    .limit(1)
                    .findFirst().orElse(0.0d).doubleValue());
        });
    }

    @Test
    public void testGetMaxRateYear () {
        Map<String, DeviceDTO> deviceDTOMap = loadDevices();
        LinkedHashMap<Short, LinkedHashMap<String, Double>> rateMap = loadRatePerDevice();

        try {
            statisticsService.getMaxRateYear("1234");
        }
        catch (Exception e) {
            Assert.assertTrue(true);
        }

        // {device-id, {year, rate}}
        Map<String, AbstractMap.SimpleEntry<Short, Double>> maxRateMap = new HashMap<>();

        rateMap.forEach((year, map) -> {
            map.forEach((deviceName, rate) -> {
                DeviceDTO dto = deviceDTOMap.get(deviceName);
                Assert.assertNotNull(dto);

                AbstractMap.SimpleEntry<Short, Double> se = new AbstractMap.SimpleEntry<>(year, rate);
                if (!maxRateMap.containsKey(dto.getDeviceId()) || maxRateMap.get(dto.getDeviceId()).getValue() < rate) {
                    maxRateMap.put(dto.getDeviceId(), se);
                }
            });
        });

        maxRateMap.forEach((deviceId, maxRateEntry) -> {
            StatisticsResponse response = statisticsService.getMaxRateYear(deviceId);

            Assert.assertNotNull(response);

            StatisticsDTO dto = response.getStatisticsDTO();

            Assert.assertNotNull(dto);

            DeviceDTO deviceDTO = deviceDTOMap.get(dto.getDeviceName());

            Assert.assertNotNull(deviceDTO);
            Assert.assertTrue(deviceDTO.getDeviceName().equals(dto.getDeviceName()));
            Assert.assertTrue(rateMap.containsKey(dto.getYear()));

            Assert.assertTrue(maxRateEntry.getKey().shortValue() == dto.getYear());
            Assert.assertTrue(maxRateEntry.getValue().doubleValue() == dto.getRate().doubleValue());
        });
    }
}
