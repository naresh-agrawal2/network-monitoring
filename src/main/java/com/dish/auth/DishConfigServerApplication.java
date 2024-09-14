package com.dish.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.CrossOrigin;


@SpringBootApplication
@CrossOrigin
@EnableConfigServer
@EnableDiscoveryClient
public class DishConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DishConfigServerApplication.class, args);
	}

}
