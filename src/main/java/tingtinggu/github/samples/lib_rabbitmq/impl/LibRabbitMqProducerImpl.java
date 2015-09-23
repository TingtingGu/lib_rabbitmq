package tingtinggu.github.samples.lib_rabbitmq.impl;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import tingtinggu.github.samples.lib_rabbitmq.Category;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConnectionEntity;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConnector;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqException;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqProducer;
import tingtinggu.github.samples.lib_rabbitmq.MQPayload;


public class LibRabbitMqProducerImpl implements LibRabbitMqProducer {
	private static Logger log = LoggerFactory.getLogger(LibRabbitMqProducerImpl.class);
	private ExecutorService executor;
	private LibRabbitMqConnector connector;

	public LibRabbitMqProducerImpl(LibRabbitMqConnector connector, ExecutorService executor) {
		this.connector = connector;
		this.executor = executor;
	}

	public void start() {
		Exchange outExchange = connector.createAmqpExchange(connector.getRabbitMqProducerEntity().Name, 
				connector.getRabbitMqProducerEntity().Type, 
				connector.getRabbitMqProducerEntity().RoutingKey);	
		log.info("create outExchange: {}", outExchange);
		if("direct".equals(connector.getRabbitMqProducerEntity().Type)) {
			Queue outQueue = connector.createQueue(connector.getRabbitMqProducerEntity().QueueName);
			connector.createBinding(outExchange, connector.getRabbitMqProducerEntity().RoutingKey, outQueue);			
		}	
	}
	
	@Override
	public void send(final MQPayload notification) throws LibRabbitMqException {
		LibRabbitMqConnectionEntity entity = connector.getRabbitMqProducerEntity();

		try {
			connector.getRabbitTemplate().convertAndSend(entity.Name, entity.RoutingKey, notification);
		}
		catch(AmqpException e) {
			throw new LibRabbitMqException(e);
		}
	}
	
	@Override
	public List<MQPayload> fetch(MQPayload notification, int timeout, TimeUnit timeUnit) {
		RabbitTemplate rabbitTemplate = connector.getRabbitTemplate();
		LibRabbitMqConnectionEntity rqEntity = connector.getRabbitMqProducerEntity();
		MQPayload response = (MQPayload) rabbitTemplate.convertSendAndReceive(rqEntity.Name, rqEntity.RoutingKey, notification);
		if(null == response || Category.Reply != response.getEnvelope().getCategory()) {
			return null;
		}
		List<MQPayload> res = (List<MQPayload>) response.getBody();
		return res;
	}

	@Override
	public void stop() {
		executor.shutdown();
	}
}

