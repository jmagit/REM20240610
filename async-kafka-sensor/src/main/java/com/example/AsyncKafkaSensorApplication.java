package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class AsyncKafkaSensorApplication implements CommandLineRunner {
    private static final Logger LOG = Logger.getLogger(AsyncKafkaSensorApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(AsyncKafkaSensorApplication.class, args);
	}
	
	@Value("${app.topic.name}") 
	private String tema;
	@Value("${app.sensor.id}") 
	private String idSensor;
	@Value("${app.dorsales}") 
	private int dorsales;

	private Random rnd = new Random();
	@Override
	public void run(String... args) throws Exception {
		var peloton = new ArrayList<Integer>();
		for(var i=1; i <= dorsales; peloton.add(i++));
		Collections.shuffle(peloton);
		
		for(var dorsal: peloton) {
			sendEvent(tema, idSensor, dorsal.toString());
			Thread.sleep(rnd.nextInt(5) * 500);
		}
	}
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	private void sendEvent(String topic, String origen, String value) {
		kafkaTemplate.send(topic, origen, value)
			.thenAccept(result -> LOG.info(String.format("TOPIC: %s, KEY: %s, VALUE: %s, OFFSET: %s", 
					topic, origen, value, result.getRecordMetadata().offset())))
			.exceptionally(ex -> {
				LOG.severe(String.format("TOPIC: %s, KEY: %s, VALUE: %s, ERROR: %s", 
					topic, origen, value, ex.getMessage()));
				return null;
			});
	}

}
