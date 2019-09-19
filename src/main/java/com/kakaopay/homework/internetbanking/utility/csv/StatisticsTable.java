package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import lombok.Data;
import lombok.ToString;

import java.util.*;

@Data
@ToString
public class StatisticsTable {
    private LinkedHashMap<String, DeviceInformation> deviceMap = new LinkedHashMap<>();

    private LinkedHashMap<Short, StatisticsColumn> statisticsColumnMap = new LinkedHashMap<>();
}

