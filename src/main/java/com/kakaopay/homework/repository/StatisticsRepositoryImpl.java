package com.kakaopay.homework.repository;

import com.kakaopay.homework.controller.dto.StatisticsDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom {

    private static final String FIRST_RANK_STATISTICS_QUERY =
            "SELECT d.device_id AS device_id, d.device_name AS device_name, ibs.year AS year, ibsd.rate AS rate, NULL as overall_rate " +
            "  FROM internet_banking_stat_detail ibsd " +
            "  JOIN device d ON d.uid = ibsd.device_id " +
            "  JOIN internet_banking_stat ibs ON ibs.uid = ibsd.internet_banking_stat_uid " +
            " WHERE ibsd.rate IN ( " +
            "                     SELECT MAX(rate) " +
            "                       FROM internet_banking_stat_detail " +
            "                      GROUP BY internet_banking_stat_uid " +
            "                    )";

    private static final String DEVICE_FIRST_RANK_YEAR_QUERY =
            "SELECT d.device_id AS device_id, d.device_name AS device_name, ibs.year AS year, ibsd.rate AS rate, NULL as overall_rate" +
            "  FROM internet_banking_stat_detail ibsd " +
            "  JOIN device d ON d.uid = ibsd.device_id " +
            "  JOIN internet_banking_stat ibs ON ibs.uid = ibsd.internet_banking_stat_uid " +
            " WHERE d.device_id = :device_id " +
            "   AND ibsd.rate IN ( " +
            "                     SELECT MAX(rate) AS rate " +
            "                       FROM internet_banking_stat_detail " +
            "                      GROUP BY device_id " +
            "                    )";

    private static final String FETCH_FORECAST_SOURCE_QUERY =
            "SELECT ibs.year AS year, d.device_name AS device_name, ibsd.rate AS rate, ibs.rate AS overall_rate " +
            "  FROM internet_banking_stat_detail ibsd " +
            "  JOIN device d ON ibsd.device_id = d.uid " +
            "  JOIN internet_banking_stat ibs ON ibs.uid = ibsd.internet_banking_stat_uid " +
            " WHERE d.device_id = :device_id " +
            " ORDER BY ibs.year ASC";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StatisticsDTO> getFirstRankDevices () {
        Query query = entityManager.createNativeQuery(FIRST_RANK_STATISTICS_QUERY, "deviceStatisticsMapper");

        return query.getResultList();
    }

    @Override
    public StatisticsDTO getFirstRankDevice (short year) {
        StringBuilder builder = new StringBuilder(FIRST_RANK_STATISTICS_QUERY);

        builder.append(" AND YEAR = :year");

        Query query = entityManager.createNativeQuery(builder.toString(), "deviceStatisticsMapper");
        query.setParameter("year", year);

        List<StatisticsDTO> list = query.getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    @Override
    public StatisticsDTO getFirstRankYear (String deviceId) {
        Query query = entityManager.createNativeQuery(DEVICE_FIRST_RANK_YEAR_QUERY, "deviceStatisticsMapper");

        query.setParameter("device_id", deviceId);

        List<StatisticsDTO> list = query.getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    @Override
    public List<StatisticsDTO> fetchForecastSources (String deviceId) {
        Query query = entityManager.createNativeQuery(FETCH_FORECAST_SOURCE_QUERY, "deviceStatisticsMapper");

        query.setParameter("device_id", deviceId);

        return query.getResultList();
    }
}
