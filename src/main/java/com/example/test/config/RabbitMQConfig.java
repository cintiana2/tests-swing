package com.example.test.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE_NAME = "pedidos.exchange";
	public static final String INPUT_QUEUE = "pedidos.entrada.seu-nome";
	public static final String DLQ_QUEUE = "pedidos.entrada.seu-nome.dlq";
	public static final String SUCCESS_QUEUE = "pedidos.status.sucesso.seu-nome";
	public static final String FAILURE_QUEUE = "pedidos.status.falha.seu-nome";

	public static final String ROUTING_KEY_INPUT = "pedidos.entrada.seu-nome";
	public static final String ROUTING_KEY_DLQ = "pedidos.entrada.seu-nome.dlq";

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE_NAME);
	}

	@Bean
	public Queue inputQueue() {
		return QueueBuilder.durable(INPUT_QUEUE).withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
				.withArgument("x-dead-letter-routing-key", ROUTING_KEY_DLQ).build();
	}

	@Bean
	public Queue dlqQueue() {
		return QueueBuilder.durable(DLQ_QUEUE).build();
	}

	@Bean
	public Queue successQueue() {
		return QueueBuilder.durable(SUCCESS_QUEUE).build();
	}

	@Bean
	public Queue failureQueue() {
		return QueueBuilder.durable(FAILURE_QUEUE).build();
	}

	@Bean
	public Binding inputBinding(Queue inputQueue, DirectExchange exchange) {
		return BindingBuilder.bind(inputQueue).to(exchange).with(ROUTING_KEY_INPUT);
	}

	@Bean
	public Binding dlqBinding(Queue dlqQueue, DirectExchange exchange) {
		return BindingBuilder.bind(dlqQueue).to(exchange).with(ROUTING_KEY_DLQ);
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public String getExchangeName() {
		return EXCHANGE_NAME;
	}

	public String getInputQueueName() {
		return INPUT_QUEUE;
	}

	public String getSuccessStatusQueueName() {
		return SUCCESS_QUEUE;
	}

	public String getFailureStatusQueueName() {
		return FAILURE_QUEUE;
	}
}