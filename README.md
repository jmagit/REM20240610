# Curso de Microservicios: Comunicación Asíncrona

## Instalación

- [JDK](https://www.oracle.com/java/technologies/downloads/)
- [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2024-03/R/eclipse-jee-2024-03-R-win32-x86_64.zip)
- [Spring Tools 4 for Eclipse](https://spring.io/tools/)
- [Project Lombok](https://projectlombok.org/download)
- Clientes de bases de datos (opcionales)
  - [HeidiSQL](https://www.heidisql.com/download.php)
  - [MongoDB Compass](https://www.mongodb.com/try/download/compass)
- Docker (opcional pero recomendable)
  - [WSL 2 feature on Windows](https://learn.microsoft.com/es-es/windows/wsl/install)
  - [Docker Desktop](https://www.docker.com/get-started/)

## Documentación

- https://docs.spring.io/spring-boot/docs/current/reference/html/
- https://docs.spring.io/spring-data/commons/docs/current/reference/html/
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
- https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
- https://docs.spring.io/spring-data/redis/docs/current/reference/html/
- https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#spring-web
- https://docs.spring.io/spring-data/rest/docs/current/reference/html/
- https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#spring-cloud-loadbalancer
- https://docs.spring.io/spring-cloud-config/docs/current/reference/html/
- https://docs.spring.io/spring-security/reference/index.html

## Ejemplos

- https://github.com/spring-projects/spring-data-examples
- https://github.com/spring-projects/spring-data-rest-webmvc
- https://github.com/spring-projects/spring-hateoas-examples
- https://github.com/spring-projects/spring-amqp-samples
- https://github.com/rabbitmq/rabbitmq-tutorials/tree/main/spring-amqp
- https://github.com/spring-projects/spring-kafka/tree/main/samples

## Base de datos de ejemplos

- [Página principal Sakila](https://dev.mysql.com/doc/sakila/en/)
- [Diagrama de la BD Sakila](http://trifulcas.com/wp-content/uploads/2018/03/sakila-er.png)

## Paquetes Java

- https://downloads.mysql.com/archives/get/p/3/file/mysql-connector-java-5.1.49.zip  
- https://sourceforge.net/projects/hibernate/files/hibernate-orm/5.6.5.Final/hibernate-release-5.6.5.Final.zip/download

## Servidores en Docker

### Bases de datos

#### MySQL

      docker run -d --name mysql-sakila -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 jamarton/mysql-sakila

####  MongoDB

      docker run -d --name mongodb -p 27017:27017 -v .:/externo jamarton/mongodb-contactos

####  Redis

      docker run -d --name redis -p 6379:6379 redis

#### Apache Cassandra

      docker run -d --name cassandra -p 9042:9042 -v .:/externo jamarton/cassandra-videodb
      
      docker exec -it cassandra sh -c /init-db.sh

### Agentes de Mensajería

#### RabbitMQ (AMQP)

      docker run -d --name rabbitmq -p 4369:4369 -p 5671:5671 -p 5672:5672 -p 15671:15671 -p 15672:15672 -p 25672:25672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=curso rabbitmq:management-alpine

#### Kafka (docker compose)

Fichero docker-compose.yml:

    services:
    zookeeper:
        image: confluentinc/cp-zookeeper:latest
        container_name: zookeeper
        environment:
        ZOOKEEPER_CLIENT_PORT: 2181
        ZOOKEEPER_TICK_TIME: 2000
        ports:
        - 2181:2181
    
    kafka:
        image: confluentinc/cp-kafka:latest
        container_name: kafka
        depends_on:
        - zookeeper
        ports:
        - 9092:9092
        environment:
        KAFKA_BROKER_ID: 1
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
        KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
        KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
        KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
        KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    
    kafka-ui:
        image: provectuslabs/kafka-ui
        container_name: kafka-ui
        depends_on:
        - kafka
        ports:
        - 9091:8080
        environment:
        - KAFKA_CLUSTERS_0_NAME=local
        - KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:29092
        - KAFKA_CLUSTERS_0_ZOOKEEPER=localhost:2181

Comando:

      cd docker-compose\kafka && docker compose up -d

#### Apache ActiveMQ o Artemis (JMS)

      docker run -d --name activemq -p 1883:1883 -p 5672:5672 -p 8161:8161 -p 61613:61613 -p 61614:61614 -p 61616:61616 jamarton/activemq

      docker run -d --name artemis -p 1883:1883 -p 5445:5445 -p 5672:5672 -p 8161:8161 -p 9404:9404 -p 61613:61613 -p 61616:61616 jamarton/artemis
