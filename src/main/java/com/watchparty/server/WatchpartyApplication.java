package com.watchparty.server;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@SpringBootApplication
public class WatchpartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WatchpartyApplication.class, args);
	}

}
