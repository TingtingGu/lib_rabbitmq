package tingtinggu.github.samples.lib_rabbitmq;

import java.util.List;
import java.util.concurrent.TimeUnit;


public interface LibRabbitMqProducer {
	void start();
	
	/**
	 * Fire a notification to the notification service.
	 * @param notification the producer fires to the notification service.
	 */
	void send(MQPayload notification) throws LibRabbitMqException;

	/**
	 * Query and fetch a set of notifications in RPC pattern
	 * @param notification the producer sends to the service to query specific notifications.
	 * @param timeout
	 * @param timeUnit
	 * @return the set of fetched notifications.
	 */
	List<MQPayload> fetch(MQPayload notification, int timeout, TimeUnit timeUnit) throws LibRabbitMqException;
	
	/**
	 * Stop API to stop connection pool to the broker
	 */
	void stop();
}
