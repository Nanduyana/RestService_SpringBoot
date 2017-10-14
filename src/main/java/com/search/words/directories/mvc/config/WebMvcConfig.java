package com.search.words.directories.mvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.search.words.directories.logging.LoggerInterceptor;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "com.search.words.directories.service" })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	LoggerInterceptor loggingInterceptor;

	@Override
	   public void addInterceptors(InterceptorRegistry registry) {
	      // Register logging Interceptor with single path pattern
	      registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/search/rest/directory");
	   }

}
