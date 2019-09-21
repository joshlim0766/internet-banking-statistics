package com.kakaopay.homework.repository;

import com.kakaopay.homework.controller.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsRepositoryCustom {
    List<StatisticsDTO> getMaxRateStat ();

    StatisticsDTO getMaxRateStatByYear (short year);

    StatisticsDTO getMaxRateYearByDevice (String deviceId);
}
