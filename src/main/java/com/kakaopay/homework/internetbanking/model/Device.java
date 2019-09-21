package com.kakaopay.homework.internetbanking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(
        name = "device"
)
public class Device {

    public Device(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "uid")
    private Long uid;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL
    )
    private List<StatisticsDetail> statisticsDetails = new ArrayList<>();
}
