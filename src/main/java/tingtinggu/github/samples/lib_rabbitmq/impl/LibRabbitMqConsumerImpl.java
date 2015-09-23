package tingtinggu.github.samples.lib_rabbitmq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.StatefulRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.policy.*;

import tingtinggu.github.samples.lib_rabbitmq.Category;
import tingtinggu.github.samples.lib_rabbitmq.Envelope;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConnector;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConsumer;
import tingtinggu.github.samples.lib_rabbitmq.MQPayload;
import tingtinggu.github.samples.lib_rabbitmq.MQPayloadHandler;

import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;

public class LibRabbitMqConsumerImpl implements LibRabbitMqConsumer {
	private static Logger log = LoggerFactory.getLogger(LibRabbitMqConsumerImpl.class);
	
	private SimpleMessageListenerContainer listenerContainer;
	private ExecutorService executor;
	private MessageConverter messageConverter;
	private Map<String, MQPayloadHandler> handlerMap;
	private LibRabbitMqConnector connector;
	
	public LibRabbitMqConsumerImpl(LibRabbitMqConnector connector, ExecutorService executor) {
		this.connector = connector;
		this.executor = executor;
		this.messageConverter = connector.getMessageConverter();
		this.handlerMap = Maps.newConcurrentMap();
	}
	
	@Override
	public void start() {
		Exchange serviceExchange = connector.createAmqpExchange(connector.getRabbitMqConsumerEntity().Name, 
				connector.getRabbitMqConsumerEntity().Type,  connector.getRabbitMqConsumerEntity().RoutingKey);
		
		Queue serviceQueue = connector.createQueue(connector.getRabbitMqConsumerEntity().QueueName);
		connector.createBinding(serviceExchange, connector.getRabbitMqConsumerEntity().RoutingKey, serviceQueue);
		
		listenerContainer = new SimpleMessageListenerContainer();
		listenerContainer.setConnectionFactory(connector.getConnectionFactory());
		listenerContainer.setQueues(serviceQueue);
		listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
		listenerContainer.setMessageListener(new ConsumerListenerContainer());
		listenerContainer.start();
	}

	@Override
	public void register(MQPayloadHandler handler) {
		handlerMap.put(handler.getClass().getName(), handler);
	}
	
	@Override
	public void unRegister(MQPayloadHandler handler) {
		handlerMap.remove(handler.getClass().getName());
	}
	
	public boolean isActive() {
		return null != listenerContainer && listenerContainer.isActive();
	}
	
	@Override
	public void stop() {
		listenerContainer.shutdown();
		listenerContainer.destroy();
		executor.shutdown();
	}	
	
	public class ConsumerListenerContainer implements MessageListener {

		@Override
		public void onMessage(final Message message) {
			log.info("ServiceListenerContainer: getMessage: {}", message);		
			final MQPayload note = (MQPayload) messageConverter.fromMessage(message);
			for(final MQPayloadHandler handler : handlerMap.values()) {
				executor.submit(new Runnable() {

					@Override
					public void run() {
						handler.handle(note);
					}
					
				});
			}
		}
	}
}
