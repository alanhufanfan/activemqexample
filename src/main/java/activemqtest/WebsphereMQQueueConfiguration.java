package activemqtest;

import activemqtest.consumers.Consumer;
import activemqtest.producers.Producer;
import activemqtest.utils.Names;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Created by zotova on 15.07.2016.
 */
@Configuration
public class WebsphereMQQueueConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    @Qualifier("ibmConnectionFactory")
    MQQueueConnectionFactory factory;

    @Bean
    @Qualifier("ibmInputConsumer")
    Consumer ibmInputConsumer () {
        return new Consumer("ibmConsumer");
    }

    @Bean
    @Qualifier("ibmOutputConsumer")
    Consumer ibmOutputConsumer () {
        return new Consumer("ibmOutputConsumer");
    }


    @Bean
    @Qualifier("ibmInputListener")
    DefaultMessageListenerContainer ibmInputListener() {

        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(factory);
        listener.setDestinationName(Names.IBMInput);
        listener.setSessionTransacted(true);
        listener.setMessageListener(applicationContext.getBean("ibmInputConsumer"));
        listener.start();
        return listener;
    }

    @Bean
    @Qualifier("ibmOutputListener")
    DefaultMessageListenerContainer ibmOutputListener() {

        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(factory);
        listener.setDestinationName(Names.IBMOutput);
        listener.setSessionTransacted(true);
        listener.setMessageListener(applicationContext.getBean("ibmOutputConsumer"));
        listener.start();
        return listener;
    }

    @Bean
    @Qualifier("ibmQueueTemplate")
    JmsMessagingTemplate ibmQueueTemplate() {
        JmsTemplate queueTemplate = new JmsTemplate(factory);
        queueTemplate.setSessionTransacted(true);
        queueTemplate.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        queueTemplate.setPubSubDomain(false);
        JmsMessagingTemplate queueMessagingTemplate =
                new JmsMessagingTemplate(queueTemplate);
        return queueMessagingTemplate;
    }


    @Bean
    Producer inputProducer() {
        return new Producer(Names.IBMInput, (JmsMessagingTemplate)
                applicationContext.getBean("ibmQueueTemplate"));
    }

    @Bean
    Producer outputProducer() {
        return new Producer(Names.IBMOutput, (JmsMessagingTemplate)
                applicationContext.getBean("ibmQueueTemplate"));
    }

}
