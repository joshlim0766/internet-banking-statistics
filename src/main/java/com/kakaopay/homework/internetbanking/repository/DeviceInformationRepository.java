package com.kakaopay.homework.internetbanking.repository;

import com.kakaopay.homework.internetbanking.model.DeviceInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceInformationRepository extends JpaRepository<DeviceInformation, Long> {
    long countByDeviceId (String deviceId);

    long countByDeviceName (String deviceName);

    Optional<DeviceInformation> findOneByDeviceId (String deviceId);

    Optional<DeviceInformation> findOneByDeviceName (String deviceName);
}
