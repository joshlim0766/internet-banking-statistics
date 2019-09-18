package com.kakaopay.homework.internetbanking.model;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceStatisticsDTO;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(
        name = "internet_banking_stat"
)
@SqlResultSetMapping(
        name = "deviceStatisticsMapper",
        classes = {
                @ConstructorResult(
                        targetClass = DeviceStatisticsDTO.class,
                        columns = {
                                @ColumnResult(name = "uid", type = Integer.class),
                                @ColumnResult(name = "device_id", type = String.class),
                                @ColumnResult(name = "device_name", type = String.class),
                                @ColumnResult(name = "year", type = Short.class),
                                @ColumnResult(name = "rate", type = Double.class)
                        }
                )
        }
)
public class InternetBankingStatistics {
    @Column(name = "uid")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;

    @Column(name = "year")
    private short year;

    @Column(name = "rate")
    private Double rate;

    @OneToMany(
            mappedBy = "parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<InternetBankingStatisticsDetail> internetBankingStatisticsDetails = new ArrayList<>();
}
