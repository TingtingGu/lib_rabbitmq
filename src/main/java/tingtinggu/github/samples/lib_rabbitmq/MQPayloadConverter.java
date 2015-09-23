package tingtinggu.github.samples.lib_rabbitmq;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MQPayloadConverter implements MessageConverter {
	private static Logger logger = LoggerFactory.getLogger(MQPayloadConverter.class);
	private static ObjectMapper mapper; 
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	
	}
	
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties)
			throws MessageConversionException {	    
		
		MQPayload mqPayload = (MQPayload)object;
		MessageProperties properties = new MessageProperties();
		properties.setHeader("__TypeId__", MQPayload.class.getName());
		properties.setContentType("application/json");
		properties.setContentEncoding("UTF-8");	
		
		String jsonMsg;
		try {
			jsonMsg = mapper.writeValueAsString(mqPayload);
			byte[] body = jsonMsg.getBytes();
			Message message = new Message(body, properties);
			return message;	
		} catch (JsonProcessingException e) {
			throw new MessageConversionException("failed to convert to message", e);
		}
	}

	@Override
	public MQPayload fromMessage(Message message)
			throws MessageConversionException {		
		Map<String, Object> headers = message.getMessageProperties().getHeaders();
		boolean needConvert = false;
		if(null == headers || !headers.containsKey("__TypeId__")) {
			needConvert = true;
		}
		else if(headers.get("__TypeId__").equals(MQPayload.class.getName())) {
			needConvert = true;
		}
		if(!needConvert) {
			return null;
		}
		
		try {
			return mapper.readValue(message.getBody(), MQPayload.class);
		} catch (Exception e) {
			throw new MessageConversionException("failed to convert to message", e);
		} 
	}

}
