package com.example;

import java.util.logging.Logger;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import com.example.models.MessageDTO;
import com.example.models.Store;

@Configuration
public class ReceptorConfig {
    private static final Logger LOGGER = Logger.getLogger(ReceptorConfig.class.getName());
    
    @RabbitListener(queues = "${app.cola}")
    public void listener(MessageDTO in) {
    	Store.add(in);
    	LOGGER.warning("RECIBIDO: " + in);
    }

}
