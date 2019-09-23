package com.kakaopay.homework.aspect;



import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Slf4j
@Aspect
@Component
public class JwtTokenServiceAspect {
    @Around("execution(* *(.., @com.kakaopay.homework.annotation.Authorization (*), ..))")
    public Object aroundResetToken (ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] params = Stream.of(joinPoint.getArgs())
                .map(arg -> {
                    if (arg instanceof String) {
                        String[] array = ((String) arg).split(" ");
                        if (array.length >= 2) arg = array[1];
                    }

                    return arg;
                }).toArray();

        return joinPoint.proceed(params);
    }
}