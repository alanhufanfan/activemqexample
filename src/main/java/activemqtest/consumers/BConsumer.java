package activemqtest.consumers;

import activemqtest.services.XmlValidator;
import activemqtest.utils.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * Created by zotova on 06.07.2016.
 */
@Component
public class BConsumer  implements MessageListener {

    @Autowired
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate;

    @Autowired
    XmlValidator validator;

//    @JmsListener(destination = Names.BQueueName, containerFactory = "jmsContainerFactory")
    @Override
    public void onMessage(javax.jms.Message message ) {

        if (validator.validate(message.toString())) {
            System.out.println("------------------" + Names.BQueueName + " consumer got message--------------------");
            System.out.println(message);
        }
        else {
            throw JmsUtils.convertJmsAccessException(
                    new JMSException("Message in " + Names.BQueueName
                            + " consumer is not valid! Abort transaction"));
        }
    }
}
