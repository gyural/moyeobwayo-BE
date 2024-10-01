package com.moyeobwayo.moyeobwayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 스케줄링 활성화를 위해

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
public class MoyeobwayoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoyeobwayoApplication.class, args);
	}

}
