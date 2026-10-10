package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.taskflow.model")
@EnableJpaRepositories("com.taskflow.repository")
public class TaskflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskflowBackendApplication.class, args);
	}

}
