package activemqtest;

import activemqtest.consumers.Consumer;
import activemqtest.consumers.RedirectConsumer;
import activemqtest.producers.Producer;
import activemqtest.utils.Names;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;

/**
 * Created by zotova on 15.07.2016.
 */
@Configuration
public class ActiveMQQueueConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("activeMQConnectionFactory")
    ActiveMQConnectionFactory factory;

    @Bean
    DefaultMessageListenerContainer aListener() {

        DefaultMessageListenerContainer aListener = new DefaultMessageListenerContainer();
        aListener.setConnectionFactory(factory);
        aListener.setPubSubDomain(false);
        aListener.setDestinationName(Names.AQueueName);
        aListener.setMessageListener(applicationContext.getBean("aConsumer"));
        aListener.setSessionTransacted(true);
        aListener.start();
        return aListener;
    }


    @Bean
    DefaultMessageListenerContainer bListener() {

        DefaultMessageListenerContainer bListener = new DefaultMessageListenerContainer();
        bListener.setConnectionFactory(factory);
        bListener.setPubSubDomain(false);
        bListener.setDestinationName(Names.BQueueName);
        bListener.setMessageListener(applicationContext.getBean("bConsumer"));
        bListener.setSessionTransacted(true);
        bListener.start();
        return  bListener;
    }

    @Bean
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate() {
        JmsTemplate queueTemplate = new JmsTemplate(factory);
        queueTemplate.setSessionTransacted(true);
        queueTemplate.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        queueTemplate.setPubSubDomain(false);
        JmsMessagingTemplate queueMessagingTemplate =
                new JmsMessagingTemplate(queueTemplate);
        return queueMessagingTemplate;
    }

    @Bean
    @Qualifier("bConsumer")
    Consumer bConsumer () {
        return new Consumer("B Consumer");
    }

    @Bean
    Producer aProducer() {
        return new Producer(Names.AQueueName, (JmsMessagingTemplate)
                applicationContext.getBean("queueTemplate"));
    }

    @Bean
    Producer bProducer() {
        return new Producer(Names.BQueueName, (JmsMessagingTemplate)
                applicationContext.getBean("queueTemplate"));
    }


    @Bean
    @Qualifier("aConsumer")
    Consumer aConsumer() {
        return new RedirectConsumer("A Consumer",
                (Producer)applicationContext.getBean("bProducer"));
    }


}
