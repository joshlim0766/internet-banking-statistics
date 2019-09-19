package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import com.kakaopay.homework.internetbanking.model.StatisticsSummary;
import com.kakaopay.homework.internetbanking.model.StatisticsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsDetailRepository extends JpaRepository<StatisticsDetail, Long> {
    long countByParentAndDeviceInformation (StatisticsSummary statistics, DeviceInformation deviceInformation);
}
