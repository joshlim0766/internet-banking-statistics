package com.kakaopay.homework.internetbanking.utility;

import com.kakaopay.homework.exception.DeviceIdGenerateException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class DeviceIdGenerator {

    private byte[] sha256 (String msg) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes("UTF-8"));

        return md.digest();
    }

    private String bytesToHex (byte[] hash) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public String generate (String deviceName) {
        try {
            UUID uuid = null;

            String source = "device-id:" + deviceName + "-" + System.currentTimeMillis();
            uuid = UUID.nameUUIDFromBytes(source.getBytes("UTF-8"));

            return bytesToHex(sha256(uuid.toString()));
        }
        catch (Exception e) {
            throw new DeviceIdGenerateException("Failed to generate device id : " + e.getMessage(), e);
        }
    }
}
