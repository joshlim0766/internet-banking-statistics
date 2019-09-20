package com.kakaopay.homework.internetbanking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(
        name = "internet_banking_stat_detail"
)
@NoArgsConstructor
public class StatisticsDetail {

    public StatisticsDetail (double rate, StatisticsSummary summary, DeviceInformation deviceInformation) {
        this.rate = rate;
        this.parent = summary;
        this.deviceInformation = deviceInformation;
    }

    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long uid;

    @Column(name = "rate")
    private Double rate;

    @ManyToOne
    @JoinColumn(name = "internet_banking_stat_uid")
    private StatisticsSummary parent;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private DeviceInformation deviceInformation;
}
