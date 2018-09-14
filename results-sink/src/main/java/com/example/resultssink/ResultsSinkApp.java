package com.example.resultssink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import java.util.function.Consumer;
import java.util.UUID;

@SpringBootApplication
public class ResultsSinkApp {

	@Autowired
	SpannerTemplate spannerTemplate;

	@Table(name = "results")
	public class Results {

		@PrimaryKey
		@Column(name = "id")
		UUID id;
		String name;
		String catnotcat;

		public Results(UUID id, String name, String catnotcat) {
			this.id = id;
			this.name = name;
			this.catnotcat = catnotcat;
		}
	}

	@Bean
	public Consumer<Message<String>> sink() {
		return (in) -> {
			String name = ""+in.getHeaders().get("ce-image-name");
			Results r = new Results(UUID.randomUUID(), name, in.getPayload());
			this.spannerTemplate.insert(r);
			System.out.println("Processed -> " + r.id + " : " + name);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ResultsSinkApp.class, args);
	}
}
