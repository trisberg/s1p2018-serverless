package com.springdeveloper.demo.functioninvoker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.deployer.EnableFunctionDeployer;

@SpringBootApplication
@EnableFunctionDeployer
public class FunctionInvokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunctionInvokerApplication.class, args);
	}
}
