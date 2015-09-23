package tingtinggu.github.samples.lib_rabbitmq;


import java.util.Map;

public class MQPayload  {
	private Envelope envelope;
	private Object body;
	
	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}
	
	public Envelope getEnvelope() {
		return this.envelope;
	}
	
	public void setBody(Object body) {
		this.body =  body;
	}
	
	public Object getBody() {
		return this.body;
	}

}
