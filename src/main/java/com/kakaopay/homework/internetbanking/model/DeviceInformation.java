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
        name = "device_info"
)
public class DeviceInformation {

    public DeviceInformation (String deviceId, String deviceName) {
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
            mappedBy = "deviceInformation",
            cascade = CascadeType.ALL
    )
    private List<StatisticsDetail> statisticsDetails = new ArrayList<>();
}
