package tingtinggu.github.samples.lib_rabbitmq;

import tingtinggu.github.samples.lib_rabbitmq.impl.LibRabbitMqConnectorImpl;


public class LibRabbitMqConnectorFactory {
	
	public static LibRabbitMqConnector createConnector(LibRabbitMqConfig config) {
		return new LibRabbitMqConnectorImpl(config);
	}
	
}
