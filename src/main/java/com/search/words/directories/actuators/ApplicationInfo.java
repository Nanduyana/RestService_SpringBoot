package com.search.words.directories.actuators;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInfo implements InfoContributor{

	//Autowire any bean and get the information from the applcation with respect to user and show it 
	
	@Autowired
	Environment env;
	
	@Override
	public void contribute(Info.Builder builder) {
		Map<String, String> appDetails = new HashMap<>();
        appDetails.put("app.name", "SearchAPI");
        builder.withDetail("app", appDetails);
	}
}
