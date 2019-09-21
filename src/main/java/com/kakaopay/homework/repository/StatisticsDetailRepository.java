package com.kakaopay.homework.repository;

import com.kakaopay.homework.model.Device;
import com.kakaopay.homework.model.StatisticsSummary;
import com.kakaopay.homework.model.StatisticsDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsDetailRepository extends JpaRepository<StatisticsDetail, Long> {
    long countByParentAndDevice(StatisticsSummary statistics, Device device);
}
