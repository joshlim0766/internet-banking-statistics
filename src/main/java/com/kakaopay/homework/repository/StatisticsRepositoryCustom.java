package com.kakaopay.homework.repository;

import com.kakaopay.homework.controller.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsRepositoryCustom {
    List<StatisticsDTO> getFirstRankDevices ();

    StatisticsDTO getFirstRankDevice (short year);

    StatisticsDTO getFirstRankYear (String deviceId);

    List<StatisticsDTO> fetchForecastSources (String deviceId);
}
