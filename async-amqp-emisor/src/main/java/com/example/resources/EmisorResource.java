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
	
	@GetMapping(path = "/saludos/{cantidad}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String saludos(@PathVariable int cantidad) throws InterruptedException {
		for(int i=1; i <= cantidad; i++) {
			amqp.convertAndSend(saludosQueue.getName(), new MessageDTO("Envio nº: " + i, origen));
			Thread.sleep(500);
		}
		return "Enviados: " + cantidad;
	}

	@GetMapping(path = "/fallido")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public String fallido() {
		amqp.convertAndSend(saludosQueue.getName(), new MessageDTO(null, origen));
		return "SEND ERROR";
	}

}
