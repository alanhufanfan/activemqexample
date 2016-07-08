package activemqtest.consumers;

import activemqtest.producers.Producer;
import activemqtest.services.XmlMarshaller;
import activemqtest.services.XmlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * Created by zotova on 06.07.2016.
 */
@Component
public class Consumer implements MessageListener{


    private String name;
    protected Producer nextProducer = null;

    public Consumer() {
    }

    public Consumer(String name) {
        this.name = name;
    }

    @Autowired
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate;

    @Autowired
    XmlMarshaller<activemqtest.domain.Message> marshaller;

    @Autowired
    XmlValidator validator;

    private String messageToString(javax.jms.Message message) throws Exception{
        return  (String)new SimpleMessageConverter().fromMessage(message);
    }

    private void printLog(String message) {
        System.out.println("------------------" + name
                + " consumer got message--------------------");
        System.out.println(message);
    }

    protected void handleMessage(javax.jms.Message message) throws Exception {

        String textMessage = messageToString(message);
        validator.validate(textMessage);
        printLog(textMessage);
        if (nextProducer != null)
            nextProducer.sendMessage(textMessage);
    }

     //JmsListener(destination = Names.AQueueName, containerFactory = "jmsContainerFactory")
    @Override
    public void onMessage(javax.jms.Message message) {
        String textMessage = "";
        try {

            handleMessage(message);
        }
        catch(Exception e) {
            throw JmsUtils.convertJmsAccessException(
                    new JMSException("Message in " + name
                            + " consumer is not valid! Abort transaction"));
        }
    }
}
