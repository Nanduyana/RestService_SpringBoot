package com.search.words.directories.actuators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Nandu Yenagandula
 * @since OCT 2017
 *
 */
@Component
public class HearBeatCheck implements HealthIndicator{
	
	@Autowired
	Environment env;
	
	@Override
	public Health health() {
		int errorCode = check(); // perform some specific health check
		if (errorCode != 0) {
			return Health.down().withDetail("Error Code", errorCode).build();
		}
		return Health.up().build();
	}

	private int check() {
		return 0;
	}

}
