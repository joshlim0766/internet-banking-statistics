package com.kakaopay.homework.internetbanking.utility;

import com.kakaopay.homework.exception.DeviceIdGenerationFailureException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class DeviceIdGenerator {
    public String generate (String deviceName) {
        try {
            UUID uuid = null;

            String source = "device-id:" + deviceName;
            uuid = UUID.nameUUIDFromBytes(source.getBytes("UTF-8"));

            return uuid.toString();
        }
        catch (Exception e) {
            throw new DeviceIdGenerationFailureException("Failed to generate device id : " + e.getMessage(), e);
        }
    }
}
