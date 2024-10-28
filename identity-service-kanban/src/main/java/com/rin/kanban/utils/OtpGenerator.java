package com.rin.kanban.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpGenerator {
    public String generateOtpCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
