package tingtinggu.github.samples.lib_rabbitmq;


public class LibRabbitMqConnectionEntity {
	public final String Name;
	public final String Type;
	public final String QueueName;
	public final String RoutingKey;	
	public final boolean Durable;
	public final boolean AutoDelete;
	
	public LibRabbitMqConnectionEntity(String dest) {
		String[] parts = dest.split(":");
		
		if(parts.length < 2) {
			throw new IllegalArgumentException(dest);
		}
		Name = parts[0];
		Type = parts[1];
		boolean durable = true;
		boolean autoDelete = false;
		String routingKey = "";
		String queueName = Name;
		if(parts.length == 4) {
			queueName = parts[2];
			routingKey = parts[3];
		}
		else if(parts.length == 3) {
			queueName = parts[2];
			if("direct".equals(Type)) {
				routingKey = queueName;
			}
			else {
				throw new IllegalArgumentException(Name);
			}
		}
		QueueName = queueName;
		RoutingKey = routingKey;
		Durable = durable;
		AutoDelete = autoDelete;
	}
}
