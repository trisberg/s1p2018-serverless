package com.example.resultssink;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class ResultsSinkApp {

	private final JdbcTemplate jdbcTemplate;

	ResultsSinkApp(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Bean
	@Transactional
	public Function<Message<String>, Message<String>> sink() {
		return (in) -> {
			System.out.println("HEADERS: " + in.getHeaders());
			Object name = in.getHeaders().get("ce-image-name");
			this.jdbcTemplate.update("insert into results(name, catnotcat) values(?, ?)", name, in.getPayload());
			return MessageBuilder.withPayload("Processed " + name).build();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ResultsSinkApp.class, args);
	}
}
