package tingtinggu.github.samples.lib_rabbitmq;

import java.util.List;



public interface LibRabbitMqConsumer {
	/**
	 * Start API to start connection pool to the broker.
	 */
	void start();
	
	/**
	 * Register a handler which is invoked when a notification is received.
	 * @param handler
	 */
	void register(MQPayloadHandler handler);
	
	void unRegister(MQPayloadHandler handler);
	
	/**
	 * Stop API to close connection pool to the broker
	 */
	void stop();
}
