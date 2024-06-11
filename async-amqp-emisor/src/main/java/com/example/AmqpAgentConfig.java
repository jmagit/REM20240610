package com.example;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpAgentConfig {
    @Bean
    MessageConverter jsonConverter() {
    	return new Jackson2JsonMessageConverter();
    }

    @Bean
    Queue saludosQueue(@Value("${app.cola}") String queue) {
//        return new Queue(queue);
    	return QueueBuilder.durable(queue)
    			.deadLetterExchange(queue + ".dlx")
    			.deadLetterRoutingKey(queue + ".dlq")
    			.build();
    }
    
    @Bean
    Queue deadLetterQueue(@Value("${app.cola}.dlq") String queue) {
        return new Queue(queue);
    }
    
    @Bean
    FanoutExchange deadLetterExchange(@Value("${app.cola}.dlx") String exchange) {
    	return new FanoutExchange(exchange);
    }
    
    @Bean
    Binding deadLetterBinding(FanoutExchange deadLetterExchange, Queue deadLetterQueue) {
    	return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }
}
