package com.search.words.directories.async.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@PropertySource("classpath:application.properties")
public class AsyncCustomizeExecutor extends AsyncConfigurerSupport{
	
	@Autowired
	private Environment env;

	@Override
	@Bean(name = "threadPoolTaskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(Integer.parseInt(env.getProperty("thread.core.pool.size")));
		taskExecutor.setMaxPoolSize(Integer.parseInt(env.getProperty("thread.max.pool.size")));
		taskExecutor.setQueueCapacity(Integer.parseInt(env.getProperty("thread.queue.capacity")));
		taskExecutor.setThreadNamePrefix(env.getProperty("thread.name.prefix"));
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

}
