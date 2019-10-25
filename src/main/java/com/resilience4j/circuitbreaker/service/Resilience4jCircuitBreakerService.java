package com.resilience4j.circuitbreaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class Resilience4jCircuitBreakerService {

	private static final Logger logger = LoggerFactory.getLogger(Resilience4jCircuitBreakerService.class);
	
	public void timeout() {
    	logger.info("Service is called...");

        throw new HttpServerErrorException(HttpStatus.GATEWAY_TIMEOUT);
    }
    
    @CircuitBreaker(name = "backendService" /*fallbackMethod = "fallback"*/)
    public void timeoutWithCircuitBreaker() {
        timeout();
    }
    
    private String fallback(Throwable ex) {
    	return "Fallback is called.";
    }
}
