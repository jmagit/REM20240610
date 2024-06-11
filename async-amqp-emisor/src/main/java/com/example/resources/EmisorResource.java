package com.example.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.models.MessageDTO;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class EmisorResource {
	private static final Logger LOGGER = Logger.getLogger(EmisorResource.class.getName());

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
		for (int i = 1; i <= cantidad; i++) {
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

	@Value("${app.rpc.routing-key}")
	String routingKey;

	@Autowired
	DirectExchange rpcExchange;

	@Autowired
	AsyncRabbitTemplate asyncRabbitTemplate;

	private Map<UUID, MessageDTO> respuestas = new HashMap<>();

	private void procesaRespuesta(Object response) {
		LOGGER.warning("Respuesta recibida: " + response);
		if (response instanceof MessageDTO m) {
			respuestas.put(m.getId(), m);
		} else {
			LOGGER.severe("Formato de respuesta desconocidor");
		}
	}

	@PostMapping(path = "/x-rpc/solicitud/{nombre}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@Operation(tags = { "web-hooks" })
	public Map<String, String> solicita(@PathVariable String nombre) {
		var peticion = new MessageDTO("Peticion de " + nombre, origen);
		asyncRabbitTemplate.convertSendAndReceive(rpcExchange.getName(), routingKey, peticion)
				.thenAccept(result -> procesaRespuesta(result))
				.exceptionally(ex -> {
					LOGGER.severe(ex.toString());
					return null;
				});
		return Map.of("status", ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(peticion.getId()).toUri().toString().replace("/" + nombre, ""));
	}

	@GetMapping(path = "/x-rpc/solicitud/{id}")
	@Operation(tags = { "web-hooks" })
	public ResponseEntity<?> respuesta(@PathVariable UUID id) {
		if(respuestas.containsKey(id)) {
			return ResponseEntity.ok(respuestas.get(id));
		} else {
			return ResponseEntity.accepted().build();
		}
	}


	@GetMapping(path = "/x-rpc/respuestas")
	@Operation(tags = { "web-hooks" })
	public Collection<MessageDTO> todasLasRespuestas() {
		return respuestas.values().stream().sorted((a, b) -> b.getEnviadoDate().compareTo(a.getEnviadoDate())).toList();
	}

}
