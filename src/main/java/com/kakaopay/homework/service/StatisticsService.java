package com.kakaopay.homework.service;

import com.kakaopay.homework.controller.dto.ForecastRequest;
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
import com.kakaopay.homework.utility.DoubleExponentialSmoothingForLinearSeries;
import com.kakaopay.homework.utility.RawStatisticsDataParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
                .map(deviceName -> new Device(idGenerator.generate(), deviceName))
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

        InputStream inputStream = null;

        File dataFile = null;

        try {
            inputStream = resource.getInputStream();
            dataFile = File.createTempFile("./test_data", ".csv");
            FileUtils.copyInputStreamToFile(inputStream, dataFile);
        }
        catch (IOException e) {
            throw new CsvParseException("Failed to parse CSV : " + e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
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

        FileUtils.deleteQuietly(dataFile);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "'InternetBankingStatisticsService.getFirstRankDevices'")
    public StatisticsResponse getFirstRankDevices () {
        StatisticsResponse response = new StatisticsResponse();

        response.setDeviceStatisticsList(statisticsRepository.getFirstRankDevices().stream()
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#year")
    public StatisticsResponse getFirstRankDevice (Short year) {
        StatisticsResponse response = new StatisticsResponse();

        StatisticsDTO dto = statisticsRepository.getFirstRankDevice(year);
        if (dto == null) {
            throw new ContentNotFoundException("Not found statistics for " + year);
        }

        response.setStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "#deviceId")
    public StatisticsResponse getFirstRankYear (String deviceId) {
        StatisticsResponse response = new StatisticsResponse();

        StatisticsDTO dto = statisticsRepository.getFirstRankYear(deviceId);
        if (dto == null) {
            throw new ContentNotFoundException("Device(" + deviceId + ") not found.");
        }

        response.setStatisticsDTO(dto);

        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key="'forecast.'.concat(#forecastRequest.deviceId)")
    public StatisticsResponse forecast (ForecastRequest forecastRequest) {
        List<StatisticsDTO> forecastSources = statisticsRepository.fetchForecastSources(forecastRequest.getDeviceId());
        if (forecastSources.size() <= 0) {
            throw new ContentNotFoundException("Not found forecast sources for device(" + forecastRequest.getDeviceId() + ")");
        }

        int population = 10000000;

        // 2019년 인구 예측
        double[] populations = forecastSources
                .stream()
                .mapToDouble(dto -> (dto.getOverallRate() / 100 * population)).toArray();
        DoubleExponentialSmoothingForLinearSeries.Model populationModel =
                DoubleExponentialSmoothingForLinearSeries.fit(populations, 0.9, 0.001);
        double[] result = populationModel.forecast(1);
        if (result == null || result.length < 1) {
            throw new RuntimeException("Couldn't forecast.");
        }

        int forecastedOverallPopulation = (int) result[0];

        double[] datas = forecastSources
                .stream()
                .mapToDouble(dto -> (dto.getOverallRate() / 100 * population) * (dto.getRate() / 100)).toArray();

        // 엑셀로 미리 구해둔 alpha 값 활용
        Map<String, Double> alphaMap = new HashMap<>();
        alphaMap.put("스마트폰", 0.25);
        alphaMap.put("데스크탑 컴퓨터", 0.75);
        alphaMap.put("노트북 컴퓨터", 0.002);
        alphaMap.put("기타", 0.5);
        alphaMap.put("스마트패드", 0.25);

        DoubleExponentialSmoothingForLinearSeries.Model model =
                DoubleExponentialSmoothingForLinearSeries.fit(
                        datas, alphaMap.get(forecastSources.get(0).getDeviceName()), 0.001);
        result = model.forecast(1);
        if (result == null || result.length < 1) {
            throw new RuntimeException("Couldn't forecast.");
        }

        int forecastedPopulation = (int) result[0];

        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setYear((short) 2019);
        statisticsDTO.setDeviceName(forecastSources.get(0).getDeviceName());
        double forecastedRate = ((double) forecastedPopulation / (double) forecastedOverallPopulation) * 100;
        if (forecastedRate < 0) forecastedRate = 0.0;
        statisticsDTO.setRate( Math.round(forecastedRate * 100) / 100.0);

        StatisticsResponse response = new StatisticsResponse();

        response.setStatisticsDTO(statisticsDTO);

        return response;
    }
}
