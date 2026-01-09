package com.parkc.kakaosender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class KakaoMessageSenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KakaoMessageSenderApplication.class, args);
    }

}
