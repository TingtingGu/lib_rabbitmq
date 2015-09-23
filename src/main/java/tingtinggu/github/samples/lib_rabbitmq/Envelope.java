package tingtinggu.github.samples.lib_rabbitmq;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class Envelope  {
	String sender;
	String receiver;
	String receiverLocation;
	int priority = 2;
	Category category = Category.Request;
	String payloadId;
	String correlatedPayloadId;
	String correlatedPayloadSender;
	String type;
	Date expiration;
	
	public Envelope() {
	}
	
	public String getPayloadId() {
		return payloadId;
	}

	public void setPayloadId(String payloadId) {
		this.payloadId = payloadId;
	}

	public String getCorrelatedPayloadId() {
		return correlatedPayloadId;
	}

	public void setCorrelatedPayloadId(String correlatedPayloadId) {
		this.correlatedPayloadId = correlatedPayloadId;
	}

	public String getCorrelatedPayloadSender() {
		return correlatedPayloadSender;
	}

	public void setCorrelatedPayloadSender(String correlatedPayloadSender) {
		this.correlatedPayloadSender = correlatedPayloadSender;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


		
	public void setReceiverLocation(String receiverLocation) {
		this.receiverLocation = receiverLocation;
	}
	
	public String getReceiverLocation() {
		return this.receiverLocation;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
	public Date getExpiration() {
		return this.expiration;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Category getCategory() {
		return this.category;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getReceiver() {
		return this.receiver;
	}
		
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
}
