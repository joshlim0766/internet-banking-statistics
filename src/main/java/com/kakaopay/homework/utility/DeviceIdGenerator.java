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

    public String generate () {
        StringBuilder sb = new StringBuilder();
        sb.append("DIS_");

        Random random = new Random();

        IntStream.range(0, 16).forEach(i -> {
            random.setSeed(secureRandom.nextLong());

            sb.append(random.nextInt(10));
        });

        return sb.toString();
    }
}
