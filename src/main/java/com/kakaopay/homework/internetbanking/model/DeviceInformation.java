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
    @Column(name = "id")
    private String deviceId;

    @Column(name = "name")
    private String deviceName;

    @OneToMany(
            mappedBy = "deviceInformation",
            cascade = CascadeType.ALL
    )
    private List<StatisticsDetail> statisticsDetails = new ArrayList<>();
}
