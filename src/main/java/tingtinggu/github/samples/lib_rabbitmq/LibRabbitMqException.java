package tingtinggu.github.samples.lib_rabbitmq;

public class LibRabbitMqException extends Exception {
	private static final long serialVersionUID = 8347782439529812105L;

	public LibRabbitMqException() {}
	
	public LibRabbitMqException(String message) {
		super(message);
	}
	
	public LibRabbitMqException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public LibRabbitMqException(Throwable cause) {
		super(cause);
	}
}
