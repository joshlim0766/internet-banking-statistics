package com.kakaopay.homework.repository;

import com.kakaopay.homework.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    long countByDeviceId (String deviceId);

    long countByDeviceName (String deviceName);

    Optional<Device> findOneByDeviceId (String deviceId);

    Optional<Device> findOneByDeviceName (String deviceName);
}
