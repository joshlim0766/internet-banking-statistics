package com.kakaopay.homework.model;

import com.kakaopay.homework.controller.dto.StatisticsDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                        targetClass = StatisticsDTO.class,
                        columns = {
                                @ColumnResult(name = "device_name", type = String.class),
                                @ColumnResult(name = "year", type = Short.class),
                                @ColumnResult(name = "rate", type = Double.class),
                                @ColumnResult(name = "overall_rate", type = Double.class)
                        }
                )
        }
)
@NoArgsConstructor
public class StatisticsSummary {

    public StatisticsSummary (short year, double rate) {
        this.year = year;
        this.rate = rate;
    }

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
    private List<StatisticsDetail> statisticsDetails = new ArrayList<>();
}
