package com.example.resources;

import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.ReceptorConfig;
import com.example.models.MessageDTO;
import com.example.models.Store;
import com.example.models.Store.Message;

@RestController
public class RecibidosResource {
	@Value("${spring.application.name}:${server.port}")
	private String origen;
	
	@Autowired
	private Queue saludosQueue;
	
	@Autowired
	private AmqpTemplate amqp;
	
	@GetMapping(path = "/recibidos")
	public List<Message> recibidos() {
		return Store.get();
	}
	
	@GetMapping(path = "/estado")
	public String cambia() {
		return "El servicio se ha " + (ReceptorConfig.cambia() ? "pausado." : "reanudado.");
	}
}
