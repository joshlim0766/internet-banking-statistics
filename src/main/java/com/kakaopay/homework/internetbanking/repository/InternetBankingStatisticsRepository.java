package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.InternetBankingStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternetBankingStatisticsRepository extends
        JpaRepository<InternetBankingStatistics, Long>, InternetBankingStatisticsRepositoryCustom {
    long countByYear (short year);

    Optional<InternetBankingStatistics> findOneByYear (short year);
}
