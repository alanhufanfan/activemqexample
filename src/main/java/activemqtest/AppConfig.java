package activemqtest;

import activemqtest.consumers.*;
import activemqtest.domain.Message;
import activemqtest.producers.Producer;
import activemqtest.services.XmlMarshaller;
import activemqtest.services.XmlValidator;
import activemqtest.utils.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.ConnectionFactory;

/**
 * Created by zotova on 06.07.2016.
 */
@Configuration
public class AppConfig {

    private static final String MESSAGE_VALIDATION_SCHEMA = "Message.xsd";

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    XmlValidator  xmlValidator() throws Exception {

        Resource schema = applicationContext.getResource(MESSAGE_VALIDATION_SCHEMA);
        return new XmlValidator(schema);
    }

    @Bean
    XmlMarshaller xmlMarshaller() throws Exception {
        Resource schema = applicationContext.getResource(MESSAGE_VALIDATION_SCHEMA);
        return new XmlMarshaller<Message>(schema, Message.class);
    }

    @Bean
    @Qualifier("jmsContainerFactory")
    JmsListenerContainerFactory<?> jmsContainerFactory(ConnectionFactory factory) {
        SimpleJmsListenerContainerFactory simpleFactory = new SimpleJmsListenerContainerFactory();
        simpleFactory.setConnectionFactory(factory);
     //   simpleFactory.setPubSubDomain(false);
        return simpleFactory;
    }


    @Bean
    @Qualifier("durableConsumer")
    Consumer durableConsumer () {
        return new Consumer("durableConsumer");
    }

    @Bean
    @Qualifier("simpleConsumer")
    Consumer simpleConsumer() {
        return new Consumer("simpleConsumer");
    }

    @Bean
    @Qualifier("bConsumer")
    Consumer bConsumer () {
        return new Consumer("B Consumer");
    }


    @Bean
    DefaultMessageListenerContainer aListener(ConnectionFactory factory) {

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
    DefaultMessageListenerContainer bListener(ConnectionFactory factory) {

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
    SimpleMessageListenerContainer durableListener(ConnectionFactory factory) {
        SimpleMessageListenerContainer dmlc = new SimpleMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setClientId("topicId1");
        dmlc.setPubSubDomain(true);
        dmlc.setDestinationName(Names.TopicName);
        dmlc.setMessageListener(applicationContext.getBean("durableConsumer"));
        dmlc.setSessionTransacted(true);
        dmlc.setDurableSubscriptionName("durable sub");
        dmlc.setSubscriptionDurable(true);
        dmlc.start();
        return dmlc;
    }

    @Bean
    SimpleMessageListenerContainer simpleListener(ConnectionFactory factory) {
        SimpleMessageListenerContainer dmlc = new SimpleMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setPubSubDomain(true);
        dmlc.setClientId("topicId2");
        dmlc.setDestinationName(Names.TopicName);
        dmlc.setMessageListener(applicationContext.getBean("simpleConsumer"));
        dmlc.setSessionTransacted(true);
        dmlc.setSubscriptionDurable(false);
        dmlc.start();
        return dmlc;
    }

    @Bean
    @Qualifier("topicProducer")
    Producer topicProducer(ConnectionFactory connectionFactory) {
        JmsTemplate topicTemplate = new JmsTemplate(connectionFactory);
        topicTemplate.setSessionTransacted(true);
        topicTemplate.setPubSubDomain(true);
        JmsMessagingTemplate topicMessagingTemplate =
                new JmsMessagingTemplate(topicTemplate);
        return new Producer(Names.TopicName, topicMessagingTemplate);
    }

    @Bean
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate queueTemplate = new JmsTemplate(connectionFactory);
        queueTemplate.setSessionTransacted(true);
        queueTemplate.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        queueTemplate.setPubSubDomain(false);
        JmsMessagingTemplate queueMessagingTemplate =
                new JmsMessagingTemplate(queueTemplate);
        return queueMessagingTemplate;
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
        return new RedirectConsumer("A Consumer", (Producer)applicationContext.getBean("bProducer"));
    }

}
