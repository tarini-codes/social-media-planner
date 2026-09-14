package com.example.social_media_planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialMediaPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaPlannerApplication.class, args);
	}
}