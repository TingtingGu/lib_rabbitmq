package tingtinggu.github.samples.lib_rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;


public interface LibRabbitMqConnector {
	
	LibRabbitMqConnectionEntity getRabbitMqConsumerEntity();
	
	LibRabbitMqConnectionEntity getRabbitMqProducerEntity();
	
	LibRabbitMqProducer createLibRabbitMqProducer();
	
	LibRabbitMqConsumer createLibRabbitMqConsumer();
	
	Exchange createAmqpExchange(String exchangeName, String exchangeType, String routingKey);
	
	RabbitTemplate getRabbitTemplate();
	
	Queue createQueue(String queueName);
	
	Binding createBinding(Exchange exchange, String routingKey, Queue queue);
	
	MessageConverter getMessageConverter();
	
	ConnectionFactory getConnectionFactory();
}
