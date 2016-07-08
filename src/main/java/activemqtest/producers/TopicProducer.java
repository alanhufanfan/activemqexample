package activemqtest.producers;

import activemqtest.domain.Message;
import activemqtest.services.XmlMarshaller;
import activemqtest.utils.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by zotova on 07.07.2016.
 */
@Component
public class TopicProducer {

    @Autowired
    XmlMarshaller marshaller;

    @Autowired
    @Qualifier("topicTemplate")
    JmsMessagingTemplate topicTemplate;

    public void sendMessage(Message message) {
        org.springframework.messaging.Message<String> m =
                MessageBuilder.withPayload(marshaller.toXml(message)).build();

        topicTemplate.send(Names.TopicName, m);

        System.out.println("-----------" + Names.TopicName + " producer has sent message ----------------");
    }

}
