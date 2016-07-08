package activemqtest.consumers;

import activemqtest.producers.BProducer;
import activemqtest.services.XmlMarshaller;
import activemqtest.services.XmlValidator;
import activemqtest.utils.Names;
import org.apache.activemq.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * Created by zotova on 06.07.2016.
 */
@Component
public class AConsumer  implements MessageListener{


    @Autowired
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate;

    @Autowired
    BProducer bProducer;

    @Autowired
    XmlMarshaller<activemqtest.domain.Message> marshaller;

    @Autowired
    XmlValidator validator;

    //JmsListener(destination = Names.AQueueName, containerFactory = "jmsContainerFactory")
    @Override
    public void onMessage(javax.jms.Message message) {

        if (validator.validate(message.toString())) {
            System.out.println("------------------" + Names.AQueueName + " consumer got message--------------------");
            System.out.println(message);
            bProducer.sendMessage(marshaller.fromXml(message.toString()));
        }
        else {
            throw JmsUtils.convertJmsAccessException(
                    new JMSException("Message in " + Names.AQueueName
                            + " consumer is not valid! Abort transaction"));
        }
    }
}
