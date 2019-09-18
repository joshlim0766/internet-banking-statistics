package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;

import java.util.List;

public interface InternetBankingStatisticsRepositoryCustom {
    List<DeviceStatisticsDTO> getMaxRateStat ();

    DeviceStatisticsDTO getMaxRateStatByYear (short year);

    DeviceStatisticsDTO getMaxRateYearByDevice (String deviceId);
}
