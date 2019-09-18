package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import com.kakaopay.homework.internetbanking.model.InternetBankingStatistics;
import com.kakaopay.homework.internetbanking.model.InternetBankingStatisticsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternetBankingStatisticsDetailRepository extends JpaRepository<InternetBankingStatisticsDetail, Long> {
    long countByParentAndDeviceInformation (InternetBankingStatistics statistics, DeviceInformation deviceInformation);
}
