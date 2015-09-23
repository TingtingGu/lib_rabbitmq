package tingtinggu.github.samples.lib_rabbitmq.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConfig;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConnectionEntity;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConnector;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqConsumer;
import tingtinggu.github.samples.lib_rabbitmq.LibRabbitMqProducer;
import tingtinggu.github.samples.lib_rabbitmq.MQPayloadConverter;


public class LibRabbitMqConnectorImpl implements LibRabbitMqConnector {
	private static Logger log = LoggerFactory.getLogger(LibRabbitMqConnectorImpl.class);	
	private final AmqpAdmin amqpAdmin;
	private final RabbitTemplate rabbitTemplate;
	private CachingConnectionFactory connectionFactory;
	private LibRabbitMqConnectionEntity out;
	private LibRabbitMqConnectionEntity in;
	private final MessageConverter messageConverter;
	
	public LibRabbitMqConnectorImpl(LibRabbitMqConfig config) {
		String rqHost = config.getHost();
        String rqUserName = config.getUserName();
        String rqPassword = config.getPassword();

        connectionFactory = new CachingConnectionFactory(rqHost);
	    connectionFactory.setUsername(rqUserName);
	    connectionFactory.setPassword(rqPassword);
	    out = new LibRabbitMqConnectionEntity(config.getOut());
	    in = new LibRabbitMqConnectionEntity(config.getIn());

	    messageConverter = new MQPayloadConverter();
	    amqpAdmin = new RabbitAdmin(connectionFactory);
	    rabbitTemplate = new RabbitTemplate();
	    rabbitTemplate.setMessageConverter(messageConverter);
	    rabbitTemplate.setConnectionFactory(connectionFactory);
	    rabbitTemplate.setExchange(out.Name);
	    rabbitTemplate.setRoutingKey(out.RoutingKey);	    
	}
	
	public MessageConverter getMessageConverter() {
		return this.messageConverter;
	}
	
	public ConnectionFactory getConnectionFactory() {
		return this.connectionFactory;
	}
	
	public AmqpAdmin getAmqpAdmin() {
		return amqpAdmin;
	}
	
	public LibRabbitMqConnectionEntity getOut() {
		return out;
	}
	
	public LibRabbitMqConnectionEntity getIn() {
		return in;
	}
	
	public RabbitTemplate getRabbitTemplate() {
		return this.rabbitTemplate;
	}
	
	public Exchange createAmqpExchange(String exchangeName, String exchangeType, String routingKey) {		
		Exchange exchange = null;
		if("direct".equals(exchangeType)) {
			exchange = new DirectExchange(exchangeName, true, false);
		}
		else if("fanout".equals(exchangeType)) {
			exchange =  new FanoutExchange(exchangeName, true, false);
		}
		else if("topic".equals(exchangeType)) {
			exchange = new TopicExchange(exchangeName, true, false);
		}
		else if(null != routingKey) {
			exchange = new TopicExchange(exchangeName, true, false);
		}
		else {
			exchange = new DirectExchange(exchangeName, true, false);
		}
		this.amqpAdmin.declareExchange(exchange);
		return exchange;
	}
	
	public Queue createQueue(String queueName) {
		if(null == queueName) {
			return amqpAdmin.declareQueue();
		}
		
		Queue queue = new Queue(queueName);
		amqpAdmin.declareQueue(queue);
		return queue;
	}
	
	public Binding createBinding(Exchange exchange, String routingKey, Queue queue) {
	       Binding binding = null;

	       if(exchange instanceof FanoutExchange) {
	           binding = BindingBuilder.bind(queue).to((FanoutExchange) exchange);
	       } else {
	    	   if(null == routingKey) {
	    		   throw new IllegalArgumentException("RoutingKey must be specified for topic or direct");
	    	   }
	    	   
	           binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
	       }

           log.info("Declaring binding {} for endpoint {}.", binding.getRoutingKey());
           this.amqpAdmin.declareBinding(binding);
	       
	       return binding;
	}
	
	@Override
	public LibRabbitMqProducer createLibRabbitMqProducer() {
		ExecutorService executor = Executors.newFixedThreadPool(20);
		LibRabbitMqProducer producer = new LibRabbitMqProducerImpl(this, executor);
		return producer;
	}

	@Override
	public LibRabbitMqConsumer createLibRabbitMqConsumer() {
		ExecutorService executor = Executors.newFixedThreadPool(20);
		LibRabbitMqConsumer consumer = new LibRabbitMqConsumerImpl(this, executor);
		return consumer;
	}

	@Override
	public LibRabbitMqConnectionEntity getRabbitMqConsumerEntity() {
		return this.in;
	}

	@Override
	public LibRabbitMqConnectionEntity getRabbitMqProducerEntity() {
		return this.out;
	}
}
