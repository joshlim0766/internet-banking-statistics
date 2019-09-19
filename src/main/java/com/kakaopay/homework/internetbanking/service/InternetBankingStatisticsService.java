package com.kakaopay.homework.internetbanking.service;

import com.kakaopay.homework.exception.CsvParseException;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.repository.DeviceInformationRepository;
import com.kakaopay.homework.internetbanking.repository.StatisticsDetailRepository;
import com.kakaopay.homework.internetbanking.repository.StatisticsRepository;
import com.kakaopay.homework.internetbanking.utility.DeviceIdGenerator;
import com.kakaopay.homework.internetbanking.utility.csv.StatisticsTable;
import com.kakaopay.homework.internetbanking.utility.csv.RawStatisticsDataParser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private DeviceIdGenerator deviceIdGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    public void init () {
    }

    @Transactional
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

        StatisticsTable statisticsTable = rawStatisticsDataParser.parse(dataFile.getAbsolutePath());

        statisticsTable.getDeviceMap().forEach((deviceId, deviceInfo) ->
                deviceInformationRepository.saveAndFlush(deviceInfo));

        statisticsTable.getStatisticsColumnMap().forEach((year, statisticsColumn) ->
                statisticsRepository.saveAndFlush(statisticsColumn.getStatisticsSummary()));

        statisticsTable.getStatisticsColumnMap().forEach((year, statisticsColumn) ->
                statisticsColumn.getStatisticsDetail().forEach((deviceId, detail) ->
                        statisticsDetailRepository.saveAndFlush(detail)));
    }

    public DeviceStatisticsResponse getDeviceStatistics (Short year) {
        return year == null ? getYearlyDeviceStatistics() : getDeviceStatisticsByYear(year);
    }

    @Transactional(readOnly = true)
    public DeviceStatisticsResponse getDevices () {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        response.setDeviceStatisticsList(deviceInformationRepository.findAll().stream()
                .map(deviceInformation -> modelMapper.map(deviceInformation, DeviceStatisticsDTO.class))
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }

    @Transactional(readOnly = true)
    public DeviceStatisticsResponse getYearlyDeviceStatistics () {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        response.setDeviceStatisticsList(statisticsRepository.getMaxRateStat().stream()
                .map(deviceStatisticsDTO -> {
                    deviceStatisticsDTO.setDeviceId(null);

                    return deviceStatisticsDTO;
                })
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }

    @Transactional(readOnly = true)
    public DeviceStatisticsResponse getDeviceStatisticsByYear (Short year) {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        DeviceStatisticsDTO dto = statisticsRepository.getMaxRateStatByYear(year);
        dto.setDeviceId(null);

        response.setDeviceStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    public DeviceStatisticsResponse getMaxRateYear (String deviceId) {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        DeviceStatisticsDTO dto = statisticsRepository.getMaxRateYearByDevice(deviceId);
        dto.setDeviceId(null);

        response.setDeviceStatisticsDTO(dto);

        return response;
    }
}
