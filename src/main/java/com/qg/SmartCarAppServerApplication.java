package com.qg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SmartCarAppServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCarAppServerApplication.class, args);
	}
}
