package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.internetbanking.model.StatisticsDetail;
import com.kakaopay.homework.internetbanking.model.StatisticsSummary;
import lombok.Data;
import lombok.ToString;

import java.util.*;

@Data
@ToString
public class StatisticsColumn {

    private StatisticsSummary statisticsSummary = new StatisticsSummary();

    // Key는 device id
    private LinkedHashMap<String, StatisticsDetail> statisticsDetail = new LinkedHashMap<>();
}
