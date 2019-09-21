package com.kakaopay.homework.internetbanking.service;

import com.kakaopay.homework.internetbanking.controller.dto.DeviceDTO;
import com.kakaopay.homework.internetbanking.controller.dto.DeviceResponse;
import com.kakaopay.homework.internetbanking.repository.DeviceInformationRepository;
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
    private DeviceInformationRepository deviceInformationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "localCache", key = "'DeviceService.getDevices'")
    public DeviceResponse getDevices () {
        DeviceResponse response = new DeviceResponse();

        response.setDeviceList(deviceInformationRepository.findAll().stream()
                .map(deviceInformation -> modelMapper.map(deviceInformation, DeviceDTO.class))
                .collect(Collectors.toCollection(ArrayList::new)));

        return response;
    }
}
