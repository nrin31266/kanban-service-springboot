package com.rin.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class KanbanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanbanServiceApplication.class, args);
	}

}
