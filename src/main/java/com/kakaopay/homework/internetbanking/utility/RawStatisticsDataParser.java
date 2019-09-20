package com.kakaopay.homework.internetbanking.utility;

import com.kakaopay.homework.exception.CsvParseException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;

@Component
public class RawStatisticsDataParser {

    @FunctionalInterface
    public interface ParserCallback {
        void parseLineCompleted (boolean isHeader, String[] row);
    }

    public void parse (String filePath, ParserCallback callback) throws CsvParseException {
        try {
            CSVReaderBuilder builder = new CSVReaderBuilder(
                    new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            CSVReader reader = builder.build();

            // Read header
            String[] row = reader.readNext();
            callback.parseLineCompleted(true, row);

            while ((row = reader.readNext()) != null) {
                callback.parseLineCompleted(false, row);
            }
        }
        catch (Exception e) {
            throw new CsvParseException("Failed to parse CSV file(" + filePath + ")", e);
        }
    }
}
