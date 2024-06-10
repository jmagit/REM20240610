package com.example;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmisorConfig {
	@Value("${app.cola}")
	private String myQueue;
	
    @Bean
    Queue saludosQueue() {
        return new Queue(myQueue);
    }
    
    @Bean
    public MessageConverter jsonConverter() {
    	return new Jackson2JsonMessageConverter();
    }
    

}
