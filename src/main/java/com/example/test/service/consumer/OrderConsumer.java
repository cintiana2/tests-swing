package com.example.test.service.consumer;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.example.test.config.LocaleConfig;
import com.example.test.config.RabbitMQConfig;
import com.example.test.enums.OrderStatus;
import com.example.test.exception.ProcessingException;
import com.example.test.service.OrderStoreService;
import com.example.test.vo.OrderRequestVO;
import com.example.test.vo.OrderStatusVO;

@Component
public class OrderConsumer {

	private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);
	private final Random random = new Random();

	private final RabbitTemplate rabbitTemplate;
	private final RabbitMQConfig rabbitMQConfig;
	private final OrderStoreService orderStoreService;
	private final MessageSource messageSource;

	public OrderConsumer(RabbitTemplate rabbitTemplate, RabbitMQConfig rabbitMQConfig,
			OrderStoreService orderStoreService, MessageSource messageSource) {
		this.rabbitTemplate = rabbitTemplate;
		this.rabbitMQConfig = rabbitMQConfig;
		this.orderStoreService = orderStoreService;
		this.messageSource = messageSource;
	}

	@RabbitListener(queues = RabbitMQConfig.INPUT_QUEUE)
	public void consumeOrder(OrderRequestVO orderRequest) {
		String msgStarted = messageSource.getMessage("order.log.started", new Object[] { orderRequest.getId() },
				LocaleConfig.PT_BR);
		log.info(msgStarted);

		orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.PROCESSING);

		try {
			int delaySeconds = 1000 + random.nextInt(2000);
			Thread.sleep(delaySeconds);

			if (random.nextDouble() < 0.20) {
				String simExcMsg = messageSource.getMessage("order.exception.simulated", null, LocaleConfig.PT_BR);
				throw new ProcessingException(simExcMsg);
			}

			String msgSuccess = messageSource.getMessage("order.log.success", new Object[] { orderRequest.getId() },
					LocaleConfig.PT_BR);
			log.info(msgSuccess);

			orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.SUCCESS);

			OrderStatusVO successStatus = OrderStatusVO.success(orderRequest.getId());
			rabbitTemplate.convertAndSend(rabbitMQConfig.getSuccessStatusQueueName(), successStatus);

		} catch (ProcessingException | InterruptedException e) {
			String msgFailed = messageSource.getMessage("order.log.failed",
					new Object[] { orderRequest.getId(), e.getMessage() }, LocaleConfig.PT_BR);
			log.error(msgFailed);

			orderStoreService.updateStatus(orderRequest.getId(), OrderStatus.FAILURE);

			OrderStatusVO failureStatus = OrderStatusVO.failure(orderRequest.getId(), e.getMessage());
			rabbitTemplate.convertAndSend(rabbitMQConfig.getFailureStatusQueueName(), failureStatus);

			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}

	
			throw new AmqpRejectAndDontRequeueException("Processamento falhou, mensagem enviada para DLQ", e);
		}
	}
}