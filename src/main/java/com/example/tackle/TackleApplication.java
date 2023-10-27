package com.example.tackle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
public class TackleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TackleApplication.class, args);
    }

}
