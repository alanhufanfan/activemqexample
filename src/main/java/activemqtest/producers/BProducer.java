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
 * Created by zotova on 06.07.2016.
 */

@Component
public class BProducer  {

    @Autowired
    XmlMarshaller marshaller;

    @Autowired
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate;


    private void send(org.springframework.messaging.Message<?> message) {
        queueTemplate.send(Names.BQueueName, message);
        System.out.println("-------------------" + Names.BQueueName + " producer has sent message ------------------");
    }

    public void sendMessage(Message message) {

        org.springframework.messaging.Message<String> m =
                MessageBuilder.withPayload(marshaller.toXml(message)).build();

        send(m);
    }

    public void sendMessage(String message) {
        org.springframework.messaging.Message<String> m =
                MessageBuilder.withPayload(message).build();

        send(m);

    }
}
