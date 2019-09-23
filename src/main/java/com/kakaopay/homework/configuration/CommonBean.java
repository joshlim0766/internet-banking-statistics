package com.kakaopay.homework.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
public class CommonBean {

    @Bean
    public ModelMapper modelMapper () {
        return new ModelMapper();
    }

    @Bean
    public SecureRandom secureRandom () throws NoSuchAlgorithmException {
        return SecureRandom.getInstance("SHA1PRNG");
    }
}
