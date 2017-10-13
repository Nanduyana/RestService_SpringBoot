package com.search.words.directories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/*
 * 
 * @author IB1583 - Nandu Yenagandula
 * @since OCT-2017
 */
@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.search.words"})
@PropertySource("classpath:application.properties")
public class SearchApplication extends SpringBootServletInitializer{
	
	public static final Logger log = LoggerFactory.getLogger(SearchApplication.class);


	public static void main(String[] args) {
		log.debug("Standalone spring boot startup");
		SpringApplication.run(SearchApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		log.debug("Spring Application Builder");
        return application.sources(SearchApplication.class);
    }
}
