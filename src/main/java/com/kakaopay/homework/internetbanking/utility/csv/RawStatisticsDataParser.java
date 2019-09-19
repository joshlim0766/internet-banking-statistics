package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.exception.CsvParseException;
import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import com.kakaopay.homework.internetbanking.model.StatisticsDetail;
import com.kakaopay.homework.internetbanking.model.StatisticsSummary;
import com.kakaopay.homework.internetbanking.utility.DeviceIdGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class RawStatisticsDataParser {

    enum ColumnIndex {
        YEAR(0), RATE(1), SMART_PHONE(2), DESKTOP(3), NOTEBOOK(4), ETC(5), SMART_PAD(6);

        private final int index;

        ColumnIndex (int index) {
            this.index = index;
        }

        public int getIndex () {
            return index;
        }
    }

    @Autowired
    private DeviceIdGenerator deviceIdGenerator;

    public StatisticsTable parse (String filePath) throws CsvParseException {
        StatisticsTable statisticsTable = null;

        try {
            CSVReaderBuilder builder = new CSVReaderBuilder(
                    new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            CSVReader reader = builder.build();

            // Read header
            String[] row = reader.readNext();
            Map<Integer, DeviceInformation> deviceMap = new HashMap<>();

            statisticsTable = new StatisticsTable();

            // Header를 이용하여 디바이스 정보 생성
            for (int i = 0; i < row.length; i++) {
                if (i < ColumnIndex.SMART_PHONE.getIndex()) continue;

                String deviceId = deviceIdGenerator.generate(row[i]);

                // UUID가 대부분의 경우에서 중복되지 않는 값을 생성하지만
                // 반드시 유일한 값을 생성한다는 보장은 없다.
                while (statisticsTable.getDeviceMap().containsKey(deviceId) == true) {
                    deviceId = deviceIdGenerator.generate(row[i]);
                }

                DeviceInformation deviceInformation = new DeviceInformation();
                deviceInformation.setDeviceId(deviceId);
                deviceInformation.setDeviceName(row[i]);

                statisticsTable.getDeviceMap().put(deviceId, deviceInformation);
                deviceMap.put(i, deviceInformation);
            }

            while ((row = reader.readNext()) != null) {
                short year = 0;
                for (int i = 0 ; i < row.length; i++) {
                    if (i == ColumnIndex.YEAR.getIndex()) {
                        year = Short.valueOf(row[i]);
                        statisticsTable.getCsvRowMap().put(year, new StatisticsColumn());
                    }
                    else if (i == ColumnIndex.RATE.getIndex()) {
                        StatisticsColumn statisticsColumn = statisticsTable.getCsvRowMap().get(year);
                        StatisticsSummary summary = statisticsColumn.getStatisticsSummary();
                        summary.setYear(year);
                        summary.setRate(Double.valueOf(row[i]));
                    }
                    else {
                        StatisticsColumn statisticsColumn = statisticsTable.getCsvRowMap().get(year);
                        StatisticsDetail detail = new StatisticsDetail();

                        DeviceInformation deviceInformation = deviceMap.get(i);

                        double rate = 0.0f;

                        try {
                            rate = Double.valueOf(row[i]);
                        }
                        catch (NumberFormatException e) {}

                        detail.setRate(rate);
                        detail.setDeviceInformation(deviceInformation);
                        detail.setParent(statisticsColumn.getStatisticsSummary());

                        statisticsColumn.getStatisticsDetail().put(deviceInformation.getDeviceId(), detail);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new CsvParseException("Failed to parse CSV file(" + filePath + ")", e);
        }

        return statisticsTable;
    }
}
