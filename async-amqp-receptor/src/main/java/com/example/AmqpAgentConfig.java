package com.example;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpAgentConfig {
    @Bean
    Queue saludosQueue(@Value("${app.cola}") String queue) {
        return new Queue(queue);
    }
    
    @Bean
    MessageConverter jsonConverter() {
    	return new Jackson2JsonMessageConverter();
    }

}
