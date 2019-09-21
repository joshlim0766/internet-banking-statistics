package com.kakaopay.homework.repository;

import com.kakaopay.homework.controller.dto.StatisticsDTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class StatisticsRepositoryImpl implements StatisticsRepositoryCustom {

    private static final String MAX_RATE_STAT_QUERY =
            "SELECT ibsd.uid AS uid, di.device_id AS device_id, di.device_name AS device_name, ibr.year AS year, ibr.rate AS rate "+
            "  FROM " +
            "       ( "+
            "        SELECT ibsg.uid AS detail_id, ibs.year AS year, ibsg.rate AS rate "+
            "          FROM internet_banking_stat AS ibs "+
            "          JOIN ( " +
            "                SELECT i.parent_id AS parent_id, i.rate AS rate, j.uid AS uid " +
            "                  FROM " +
            "                      ( " +
            "                       SELECT internet_banking_stat_uid AS parent_id, MAX(rate) AS rate " +
            "                         FROM internet_banking_stat_detail " +
            "                        GROUP  BY internet_banking_stat_uid " +
            "                      ) i, internet_banking_stat_detail j " +
            "                 WHERE i.rate = j.rate " +
            "                   AND i.parent_id = j.internet_banking_stat_uid " +
            "               ) AS ibsg ON ibsg.parent_id = ibs.uid " +
            "       ) AS ibr " +
            "  JOIN internet_banking_stat_detail AS ibsd ON ibsd.uid = ibr.detail_id " +
            "  JOIN device AS di ON di.uid = ibsd.device_id";

    private static final String MAX_RATE_YEAR_BY_DEVICE_QUERY =
            "SELECT ibsd.uid AS uid, di.device_id AS device_id, di.device_name AS device_name, ibs.year AS year, ibsdg.rate AS rate "+
            "  FROM internet_banking_stat_detail ibsd "+
            "  JOIN ( " +
            "        SELECT MAX(rate) AS rate, device_id " +
            "          FROM internet_banking_stat_detail " +
            "         group by device_id " +
            "       ) ibsdg on ibsd.device_id = ibsdg.device_id and ibsd.rate = ibsdg.rate " +
            "  JOIN device di on di.uid= ibsd.device_id " +
            "  JOIN internet_banking_stat ibs on ibsd.internet_banking_stat_uid = ibs.uid " +
            " WHERE di.device_id = :deviceId  ";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StatisticsDTO> getMaxRateStat () {
        Query query = entityManager.createNativeQuery(MAX_RATE_STAT_QUERY, "deviceStatisticsMapper");

        return query.getResultList();
    }

    @Override
    public StatisticsDTO getMaxRateStatByYear (short year) {
        StringBuilder builder = new StringBuilder(MAX_RATE_STAT_QUERY);

        builder.append(" WHERE YEAR = :year");

        Query query = entityManager.createNativeQuery(builder.toString(), "deviceStatisticsMapper");
        query.setParameter("year", year);

        List<StatisticsDTO> list = query.getResultList();

        return list.size() == 0 ? null : list.get(0);
    }

    @Override
    public StatisticsDTO getMaxRateYearByDevice(String deviceId) {
        Query query = entityManager.createNativeQuery(MAX_RATE_YEAR_BY_DEVICE_QUERY, "deviceStatisticsMapper");

        query.setParameter("deviceId", deviceId);

        List<StatisticsDTO> list = query.getResultList();

        return list.size() == 0 ? null : list.get(0);
    }
}
