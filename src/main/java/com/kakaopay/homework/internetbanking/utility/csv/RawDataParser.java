package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.exception.CsvParseException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Component
public class RawDataParser {

    public RawData parse (String fileName) throws CsvParseException {
        RawData rawData = new RawData();

        try {
            CSVReaderBuilder builder =  new CSVReaderBuilder(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            CSVReader reader = builder.build();

            // Read header
            String[] row = reader.readNext();
            List<String> header = Arrays.asList(row);
            rawData.setHeader(header);

            ColumnPositionMappingStrategy<CsvRow> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(CsvRow.class);
            strategy.captureHeader(reader);

            CsvToBean<CsvRow> csvToBean = new CsvToBean<>();

            csvToBean.setCsvReader(reader);
            csvToBean.setMappingStrategy(strategy);

            List<CsvRow> csvRows = csvToBean.parse();

            rawData.setRows(csvRows);

            return rawData;
        }
        catch (Exception e) {
            throw new CsvParseException("Failed to parse CSV file(" + fileName + ")", e);
        }
    }
}
