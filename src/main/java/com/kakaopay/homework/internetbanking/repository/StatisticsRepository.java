package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.StatisticsSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsRepository extends
        JpaRepository<StatisticsSummary, Long>, StatisticsRepositoryCustom {
    long countByYear (short year);

    Optional<StatisticsSummary> findOneByYear (short year);
}
