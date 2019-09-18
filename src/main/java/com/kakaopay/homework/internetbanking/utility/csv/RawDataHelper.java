package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import com.kakaopay.homework.internetbanking.model.InternetBankingStatistics;
import com.kakaopay.homework.internetbanking.model.InternetBankingStatisticsDetail;

import java.util.*;
import java.util.stream.Collectors;

public class RawDataHelper {

    private final RawData rawData;

    public RawDataHelper (RawData rawData) {
        this.rawData = rawData;
    }

    public List<DeviceInformation> getDeviceInformations () {
        List<String> deviceNames = rawData.getHeader()
                .stream()
                .skip(2)
                .collect(Collectors.toList());

        return deviceNames.stream()
                .map(deviceName -> {
                    DeviceInformation deviceInformation = new DeviceInformation();

                    deviceInformation.setDeviceName(deviceName);

                    return deviceInformation;
                })
                .collect(Collectors.toList());
    }

    public List<InternetBankingStatistics> getYearlyStatistics () {
        return rawData.getRows().stream()
                .map(row -> {
                    InternetBankingStatistics internetBankingStatistics = new InternetBankingStatistics();

                    internetBankingStatistics.setYear(row.getYear());
                    internetBankingStatistics.setRate(row.getRate());

                    return internetBankingStatistics;
                })
                .collect(Collectors.toList());
    }

    private InternetBankingStatisticsDetail createStatisticsDetail (CsvRow row, int position, List<String> headers) {
        Map.Entry<String, Number> entry = row.getValue(position, headers);
        if (entry == null) return null;

        InternetBankingStatisticsDetail detail = new InternetBankingStatisticsDetail();

        DeviceInformation deviceInformation = new DeviceInformation();

        deviceInformation.setDeviceName(entry.getKey());

        detail.setDeviceInformation(deviceInformation);
        detail.setRate(entry.getValue().doubleValue());

        return detail;
    }

    public Map<Short, List<InternetBankingStatisticsDetail>> getStatisticsDetail () {
        // Key는 연도, Value는 해당 연도의 디바이스별 인터넷 뱅킹 사용율의 List
        Map<Short, List<InternetBankingStatisticsDetail>> result = new LinkedHashMap<>();

        rawData.getRows().stream()
                .forEach(row -> {
                    result.put(row.getYear(), new ArrayList<>());

                    rawData.getHeader().stream()
                            .skip(2)
                            .forEach(header -> {
                                List<String> headers = rawData.getHeader();
                                int position = rawData.getHeader().indexOf(header);

                                InternetBankingStatisticsDetail detail = createStatisticsDetail(row, position, headers);
                                if (detail == null) return;

                                result.get(row.getYear()).add(detail);
                            });
                });

        return result;
    }
}
