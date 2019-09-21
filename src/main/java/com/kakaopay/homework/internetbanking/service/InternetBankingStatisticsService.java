package com.kakaopay.homework.internetbanking.service;

import com.kakaopay.homework.exception.CsvParseException;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import com.kakaopay.homework.internetbanking.model.StatisticsDetail;
import com.kakaopay.homework.internetbanking.model.StatisticsSummary;
import com.kakaopay.homework.internetbanking.repository.DeviceInformationRepository;
import com.kakaopay.homework.internetbanking.repository.StatisticsDetailRepository;
import com.kakaopay.homework.internetbanking.repository.StatisticsRepository;
import com.kakaopay.homework.internetbanking.utility.DeviceIdGenerator;
import com.kakaopay.homework.internetbanking.utility.RawStatisticsDataParser;
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
public class InternetBankingStatisticsService {

    @Autowired
    private DeviceInformationRepository deviceInformationRepository;

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

    private void createDeviceInformation (String[] row, List<DeviceInformation> deviceInformations) {
        IntStream.range(2, row.length)
                .mapToObj(i -> row[i])
                .map(deviceName -> new DeviceInformation(idGenerator.generate(deviceName), deviceName))
                .forEach(deviceInformation -> {
                    deviceInformationRepository.saveAndFlush(deviceInformation);
                    deviceInformations.add(deviceInformation);
                });
    }

    private StatisticsSummary createStatisticsSummary (String[] row) {
        StatisticsSummary summary = new StatisticsSummary(Short.valueOf(row[0]), Double.valueOf(row[1]));

        statisticsRepository.saveAndFlush(summary);

        return summary;
    }

    private void createStatisticsDetail (String[] row, StatisticsSummary summary, List<DeviceInformation> deviceInformations) {
        String regex = "[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)([eE]\\d+)?";
        List<Double> rates = IntStream.range(2, row.length)
                .mapToObj(i -> row[i])
                .map(value -> value.matches(regex) ? Double.valueOf(value) : 0.0f)
                .collect(Collectors.toList());

        IntStream.range(0, rates.size())
                .mapToObj(i -> new StatisticsDetail(rates.get(i), summary, deviceInformations.get(i)))
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

        deviceInformationRepository.deleteAll();
        statisticsRepository.deleteAll();
        statisticsDetailRepository.deleteAll();

        List<DeviceInformation> deviceInformations = new ArrayList<>();

        rawStatisticsDataParser.parse(dataFile.getAbsolutePath(), (isHeader, row) -> {
            if (isHeader) {
                createDeviceInformation(row, deviceInformations);
            }
            else {
                StatisticsSummary summary = createStatisticsSummary(row);
                createStatisticsDetail(row, summary, deviceInformations);
            }
        });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "'InternetBankingStatisticsService.getYearlyDeviceStatistics'")
    public DeviceStatisticsResponse getYearlyDeviceStatistics () {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        response.setDeviceStatisticsList(statisticsRepository.getMaxRateStat().stream()
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#year")
    public DeviceStatisticsResponse getDeviceStatisticsByYear (Short year) {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        DeviceStatisticsDTO dto = statisticsRepository.getMaxRateStatByYear(year);
        if (dto == null) {
            throw new RuntimeException("Not found statistics for " + year);
        }

        response.setDeviceStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#deviceId")
    public DeviceStatisticsResponse getMaxRateYear (String deviceId) {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        DeviceStatisticsDTO dto = statisticsRepository.getMaxRateYearByDevice(deviceId);
        if (dto == null) {
            throw new RuntimeException("Device(" + deviceId + ") not found.");
        }

        response.setDeviceStatisticsDTO(dto);

        return response;
    }
}
