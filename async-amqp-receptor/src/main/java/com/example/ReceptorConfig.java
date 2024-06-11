package com.example;

import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.example.models.MessageDTO;
import com.example.models.Store;

@Configuration
public class ReceptorConfig {
    private static final Logger LOGGER = Logger.getLogger(ReceptorConfig.class.getName());
    
    private static boolean pausa=false;
    public static boolean cambia() {
    	return pausa = !pausa;		
	}
    
    @RabbitListener(queues = "${app.cola}")
    public void listener(MessageDTO in) throws InterruptedException {
    	if(pausa) {
    		throw new AmqpException("En pausa");
    	}
    	if(in.getMsg() == null) {
    		throw new AmqpRejectAndDontRequeueException("Mensaje invalido.");
    	}
    	Store.add(in);
    	Thread.sleep(in.getMsg().length() * 500);
    	LOGGER.warning("RECIBIDO: " + in);
    }

	@Value("${spring.application.name}:${server.port}")
	private String origen;

    private Random rnd = new Random();
    
    @RabbitListener(queues = "${app.rpc.queue}")
    public MessageDTO responde(MessageDTO in) throws InterruptedException {
    	LOGGER.warning("SOLICITUD RECIBIDA: " + in);
    	Thread.sleep(in.getMsg().length() * 500);
    	LOGGER.warning("RESPONDIENDO EN: " + new Date());
    	var out = new MessageDTO((rnd.nextInt(2) == 0 ? "ACEPTADA -> " : "RECHAZADA -> ") + in.toString(), origen);
    	out.setId(in.getId());
    	return out;
    }

    
}
