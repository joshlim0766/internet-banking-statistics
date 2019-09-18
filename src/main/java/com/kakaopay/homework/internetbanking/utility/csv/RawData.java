package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.internetbanking.model.InternetBankingStatistics;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class RawData {
    private List<String> header = new ArrayList<>();

    private List<CsvRow> rows = new ArrayList<>();

}

