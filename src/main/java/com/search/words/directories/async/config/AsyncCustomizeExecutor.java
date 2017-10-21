package com.search.words.directories.async.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@PropertySource("classpath:application.properties")
public class AsyncCustomizeExecutor extends AsyncConfigurerSupport{

	@Override
	@Bean(name = "threadPoolTaskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setQueueCapacity(20);
		taskExecutor.setThreadNamePrefix("Async-");
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

}
