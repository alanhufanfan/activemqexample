package activemqtest;

import activemqtest.consumers.*;
import activemqtest.domain.Message;
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
    TopicConsumer durableConsumer () {
        return new TopicConsumer("durableConsumer");
    }

    @Bean
    TopicConsumer simpleConsumer() {
        return new TopicConsumer("simpleConsumer");
    }


    @Bean
    AConsumer aConsumer() {
        return new AConsumer();
    }

    @Bean
    BConsumer bConsumer () {
        return new BConsumer();
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
        TopicConsumer durableConsumer = (TopicConsumer)applicationContext.getBean("durableConsumer");
        SimpleMessageListenerContainer dmlc = new SimpleMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setClientId("topicId1");
        dmlc.setPubSubDomain(true);
        dmlc.setDestinationName(Names.TopicName);
        dmlc.setMessageListener(durableConsumer);
        dmlc.setSessionTransacted(true);
        dmlc.setDurableSubscriptionName(durableConsumer.getName());
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
    @Qualifier("topicTemplate")
    JmsMessagingTemplate topicMessagingTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate topicTemplate = new JmsTemplate(connectionFactory);
        topicTemplate.setSessionTransacted(true);
        topicTemplate.setPubSubDomain(true);
        JmsMessagingTemplate topicMessagingTemplate =
                new JmsMessagingTemplate(topicTemplate);
        return topicMessagingTemplate;
    }

    @Bean
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueMessagingTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate queueTemplate = new JmsTemplate(connectionFactory);
        queueTemplate.setSessionTransacted(true);
        queueTemplate.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.AUTO.getMode());
        queueTemplate.setPubSubDomain(false);
        JmsMessagingTemplate queueMessagingTemplate =
                new JmsMessagingTemplate(queueTemplate);
        return queueMessagingTemplate;
    }
}
