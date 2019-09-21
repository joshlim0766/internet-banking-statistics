package com.kakaopay.homework.service;

import com.kakaopay.homework.exception.ContentNotFoundException;
import com.kakaopay.homework.exception.CsvParseException;
import com.kakaopay.homework.controller.dto.StatisticsDTO;
import com.kakaopay.homework.controller.dto.StatisticsResponse;
import com.kakaopay.homework.model.Device;
import com.kakaopay.homework.model.StatisticsDetail;
import com.kakaopay.homework.model.StatisticsSummary;
import com.kakaopay.homework.repository.DeviceRepository;
import com.kakaopay.homework.repository.StatisticsDetailRepository;
import com.kakaopay.homework.repository.StatisticsRepository;
import com.kakaopay.homework.utility.DeviceIdGenerator;
import com.kakaopay.homework.utility.RawStatisticsDataParser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class StatisticsService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private StatisticsDetailRepository statisticsDetailRepository;

    @Autowired
    private RawStatisticsDataParser rawStatisticsDataParser;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DeviceIdGenerator idGenerator;

    private void createDeviceInformation (String[] row, List<Device> devices) {
        IntStream.range(2, row.length)
                .mapToObj(i -> row[i])
                .map(deviceName -> new Device(idGenerator.generate(deviceName), deviceName))
                .forEach(device -> {
                    deviceRepository.saveAndFlush(device);
                    devices.add(device);
                });
    }

    private StatisticsSummary createStatisticsSummary (String[] row) {
        StatisticsSummary summary = new StatisticsSummary(Short.valueOf(row[0]), Double.valueOf(row[1]));

        statisticsRepository.saveAndFlush(summary);

        return summary;
    }

    private void createStatisticsDetail (String[] row, StatisticsSummary summary, List<Device> devices) {
        String regex = "[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)([eE]\\d+)?";
        List<Double> rates = IntStream.range(2, row.length)
                .mapToObj(i -> row[i])
                .map(value -> value.matches(regex) ? Double.valueOf(value) : 0.0d)
                .collect(Collectors.toList());

        IntStream.range(0, rates.size())
                .mapToObj(i -> new StatisticsDetail(rates.get(i), summary, devices.get(i)))
                .forEach(detail -> statisticsDetailRepository.saveAndFlush(detail));
    }

    @Transactional
    @CacheEvict(value = "localCache", allEntries = true)
    public void loadData () {
        ClassPathResource resource = new ClassPathResource("data.csv");

        File dataFile = null;

        try {
            dataFile = resource.getFile();
        }
        catch (IOException e) {
            throw new CsvParseException("Failed to parse CSV : " + e.getMessage(), e);
        }

        deviceRepository.deleteAll();
        statisticsRepository.deleteAll();
        statisticsDetailRepository.deleteAll();

        List<Device> devices = new ArrayList<>();

        rawStatisticsDataParser.parse(dataFile.getAbsolutePath(), (isHeader, row) -> {
            if (isHeader) {
                createDeviceInformation(row, devices);
            }
            else {
                StatisticsSummary summary = createStatisticsSummary(row);
                createStatisticsDetail(row, summary, devices);
            }
        });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "'InternetBankingStatisticsService.getYearlyDeviceStatistics'")
    public StatisticsResponse getYearlyDeviceStatistics () {
        StatisticsResponse response = new StatisticsResponse();

        response.setDeviceStatisticsList(statisticsRepository.getMaxRateStat().stream()
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#year")
    public StatisticsResponse getDeviceStatisticsByYear (Short year) {
        StatisticsResponse response = new StatisticsResponse();

        StatisticsDTO dto = statisticsRepository.getMaxRateStatByYear(year);
        if (dto == null) {
            throw new ContentNotFoundException("Not found statistics for " + year);
        }

        response.setStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#deviceId")
    public StatisticsResponse getMaxRateYear (String deviceId) {
        StatisticsResponse response = new StatisticsResponse();

        StatisticsDTO dto = statisticsRepository.getMaxRateYearByDevice(deviceId);
        if (dto == null) {
            throw new ContentNotFoundException("Device(" + deviceId + ") not found.");
        }

        response.setStatisticsDTO(dto);

        return response;
    }
}
