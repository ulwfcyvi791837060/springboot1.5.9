package com.yyx.aio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@MapperScan("com.yyx.aio.mapper")
public class AIOMApplication {

	public static void main(String[] args) {
		 new SpringApplicationBuilder(AIOMApplication.class).run(args);
	}
}
