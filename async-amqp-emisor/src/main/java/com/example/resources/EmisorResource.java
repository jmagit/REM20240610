package com.example.resources;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.models.MessageDTO;

@RestController
public class EmisorResource {
	@Value("${spring.application.name}:${server.port}")
	private String origen;
	
	@Autowired
	private Queue saludosQueue;
	
	@Autowired
	private AmqpTemplate amqp;
	
	@GetMapping(path = "/saludo/{nombre}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String saluda(@PathVariable String nombre) {
		var msg = "Hola " + nombre;
		amqp.convertAndSend(saludosQueue.getName(), new MessageDTO(msg, origen));
		return "SEND: " + msg;
	}
}
