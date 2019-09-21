package com.kakaopay.homework.service;

import com.kakaopay.homework.controller.dto.DeviceDTO;
import com.kakaopay.homework.controller.dto.DeviceResponse;
import com.kakaopay.homework.repository.DeviceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "'DeviceService.getDevices'")
    public DeviceResponse getDevices () {
        DeviceResponse response = new DeviceResponse();

        response.setDeviceList(deviceRepository.findAll().stream()
                .map(deviceInformation -> modelMapper.map(deviceInformation, DeviceDTO.class))
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }
}
