package com.kakaopay.homework.utility;

import com.kakaopay.homework.exception.DeviceIdGenerateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DeviceIdGenerator {

    @Autowired
    private SecureRandom secureRandom;

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
        StringBuilder sb = new StringBuilder();
        sb.append("DIS_");

        Random random = new Random();

        IntStream.range(0, 10).forEach(i -> {
            random.setSeed(secureRandom.nextLong());

            sb.append(random.nextInt(10));
        });

        return sb.toString();
    }
}
