package com.kakaopay.homework.internetbanking.service;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsResponse;
import com.kakaopay.homework.internetbanking.repository.DeviceInformationRepository;
import com.kakaopay.homework.internetbanking.repository.InternetBankingStatisticsDetailRepository;
import com.kakaopay.homework.internetbanking.repository.InternetBankingStatisticsRepository;
import com.kakaopay.homework.internetbanking.utility.DeviceIdGenerator;
import com.kakaopay.homework.internetbanking.utility.csv.RawData;
import com.kakaopay.homework.internetbanking.utility.csv.RawDataHelper;
import com.kakaopay.homework.internetbanking.utility.csv.RawDataParser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InternetBankingStatisticsService {

    @Autowired
    private DeviceInformationRepository deviceInformationRepository;

    @Autowired
    private InternetBankingStatisticsRepository internetBankingStatisticsRepository;

    @Autowired
    private InternetBankingStatisticsDetailRepository internetBankingStatisticsDetailRepository;

    @Autowired
    private RawDataParser rawDataParser;

    @Autowired
    private DeviceIdGenerator deviceIdGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    public void init () {
        RawData rawData = rawDataParser.parse("./data.csv");

        createDeviceInformations(rawData);
        createInternetBankingStatistics(rawData);
        createInternetBankingStatisticsDetail(rawData);
    }

    @Transactional
    protected void createDeviceInformations (RawData rawData) {
        RawDataHelper helper = new RawDataHelper(rawData);

        helper.getDeviceInformations().stream()
                .filter(deviceInformation -> deviceInformationRepository.countByDeviceName(deviceInformation.getDeviceName()) == 0)
                .collect(Collectors.toList()).stream()
                .map(deviceInformation -> {
                    String deviceName = deviceInformation.getDeviceName();
                    String deviceId = deviceIdGenerator.generate(deviceName);

                    // 확률은 매우 희박하지만 동일한 UUID가 중복 생성되는 가능성이 있음.
                    while (deviceInformationRepository.countByDeviceId(deviceId) != 0) {
                        deviceId = deviceIdGenerator.generate(deviceName);
                    }

                    deviceInformation.setDeviceId(deviceId);

                    return deviceInformation;
                })
                .forEach(deviceInformation -> deviceInformationRepository.saveAndFlush(deviceInformation));
    }

    @Transactional
    protected void createInternetBankingStatistics (RawData rawData) {
        RawDataHelper helper = new RawDataHelper(rawData);

        helper.getYearlyStatistics().stream()
                .filter(statistics -> internetBankingStatisticsRepository.countByYear(statistics.getYear()) == 0)
                .collect(Collectors.toList()).stream()
                .forEach(statistics -> internetBankingStatisticsRepository.saveAndFlush(statistics));
    }

    @Transactional
    protected void createInternetBankingStatisticsDetail (RawData rawData) {
        RawDataHelper helper = new RawDataHelper(rawData);
        helper.getStatisticsDetail().forEach((key, value) -> {
            value.stream()
                    .map(detail -> {
                        deviceInformationRepository.findOneByDeviceName(detail.getDeviceInformation().getDeviceName())
                                .ifPresent(deviceInformation -> detail.setDeviceInformation(deviceInformation));
                        internetBankingStatisticsRepository.findOneByYear(key)
                                .ifPresent(statistics -> detail.setParent(statistics));

                        return detail;
                    })
                    .filter(detail -> (detail.getDeviceInformation() != null || detail.getParent() != null))
                    .collect(Collectors.toList()).stream()
                    .filter(detail -> (internetBankingStatisticsDetailRepository.countByParentAndDeviceInformation(
                                detail.getParent(), detail.getDeviceInformation()) == 0))
                    .collect(Collectors.toList()).stream()
                    .forEach(detail -> internetBankingStatisticsDetailRepository.saveAndFlush(detail));
        });
    }

    public void loadData () {
        RawData rawData = rawDataParser.parse("./data.csv");

        createDeviceInformations(rawData);
        createInternetBankingStatistics(rawData);
        createInternetBankingStatisticsDetail(rawData);
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

        response.setDeviceStatisticsList(internetBankingStatisticsRepository.getMaxRateStat().stream()
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

        DeviceStatisticsDTO dto = internetBankingStatisticsRepository.getMaxRateStatByYear(year);
        dto.setDeviceId(null);

        response.setDeviceStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    public DeviceStatisticsResponse getMaxRateYear (String deviceId) {
        DeviceStatisticsResponse response = new DeviceStatisticsResponse();

        DeviceStatisticsDTO dto = internetBankingStatisticsRepository.getMaxRateYearByDevice(deviceId);
        dto.setDeviceId(null);

        response.setDeviceStatisticsDTO(dto);

        return response;
    }
}
