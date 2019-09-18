package com.kakaopay.homework.internetbanking.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(
        name = "internet_banking_stat_detail"
)
public class InternetBankingStatisticsDetail {
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long uid;

    @Column(name = "rate")
    private Double rate;

    @ManyToOne
    @JoinColumn(name = "internet_banking_stat_uid")
    private InternetBankingStatistics parent;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private DeviceInformation deviceInformation;
}
