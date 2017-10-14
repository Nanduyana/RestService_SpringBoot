package com.search.words.directories.logging;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.search.words.directories.SearchDirectoriesBean;

@Component("loggingInterceptor")
public class LoggerInterceptor extends HandlerInterceptorAdapter{

	public static final Logger log = LoggerFactory.getLogger(SearchDirectoriesBean.class);

	static{
		BasicConfigurator.configure();
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		//logRequest(request);
		return super.preHandle(request, response, handler);
	}
	
	public void logRequest(HttpServletRequest request) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
	    try {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }
	    } finally {
	        reader.close();
	    }
	    log.info(sb.toString());
	}
	
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		long startTime = (Long)request.getAttribute("startTime");

		long endTime = System.currentTimeMillis();

		long executeTime = endTime - startTime;
		
		log.debug("request time :: {}", executeTime);
		
		super.postHandle(request, response, handler, modelAndView);
	}
}
