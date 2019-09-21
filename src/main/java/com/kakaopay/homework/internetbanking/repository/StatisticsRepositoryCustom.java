package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.controller.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsRepositoryCustom {
    List<StatisticsDTO> getMaxRateStat ();

    StatisticsDTO getMaxRateStatByYear (short year);

    StatisticsDTO getMaxRateYearByDevice (String deviceId);
}
