package com.resilience4j.circuitbreaker.config;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.resilience4j.circuitbreaker.service.Resilience4jCircuitBreakerService;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class Resilience4jCircuitBreakerConfig {

	private static final Logger logger = LoggerFactory.getLogger(Resilience4jCircuitBreakerConfig.class);
	
	private static final Consumer<Runnable> consumer = runnable -> IntStream.range(0, 5).forEach(value -> {
        try {
            runnable.run();
        } catch (CircuitBreakerOpenException e) {
        	logger.warn("Circuit breaker is applied");
        } catch (Exception e) {
        	logger.warn("Exception occured during service invocation");
        }
    });
	
	@Bean
    public CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        return circuitBreakerRegistry.circuitBreaker("default");
    }
	
	@Bean
    public ApplicationRunner applicationRunner(Resilience4jCircuitBreakerService resilience4jCircuitBreakerService, CircuitBreaker defaultCircuitBreaker) {
        return applicationArguments -> {
            
        	logger.info("Service is called without applying circuit breaker...");
            consumer.accept(() -> resilience4jCircuitBreakerService.timeout());
            
            logger.info("Service is called applying circuit breaker using annotations...");
            consumer.accept(() -> resilience4jCircuitBreakerService.timeoutWithCircuitBreaker());
            
            logger.info("Service is called applying default circuit breaker using manual invocation...");
            Runnable decoratedRunnable = CircuitBreaker.decorateRunnable(defaultCircuitBreaker, () -> resilience4jCircuitBreakerService.timeout());
            consumer.accept(() -> decoratedRunnable.run());
        };
    }
}
