package activemqtest;

import activemqtest.consumers.*;
import activemqtest.domain.Message;
import activemqtest.producers.Producer;
import activemqtest.services.XmlMarshaller;
import activemqtest.services.XmlValidator;
import activemqtest.utils.Names;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
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
import javax.jms.JMSException;

import com.ibm.jms.*;
import com.ibm.mq.*;

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
    @Qualifier("activeMQConnectionFactory")
    ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        activeMQConnectionFactory.setUserName("admin");
        activeMQConnectionFactory.setPassword("admin");

        RedeliveryPolicy queuePolicy = new RedeliveryPolicy();
        queuePolicy.setInitialRedeliveryDelay(0);
        queuePolicy.setRedeliveryDelay(1000);
        queuePolicy.setUseExponentialBackOff(false);
        queuePolicy.setMaximumRedeliveries(2);

        RedeliveryPolicy topicPolicy = new RedeliveryPolicy();
        topicPolicy.setInitialRedeliveryDelay(0);
        topicPolicy.setRedeliveryDelay(1000);
        topicPolicy.setUseExponentialBackOff(false);
        topicPolicy.setMaximumRedeliveries(3);

// Receive a message with the JMS API
        RedeliveryPolicyMap map = activeMQConnectionFactory.getRedeliveryPolicyMap();
        map.put(new ActiveMQTopic(">"), topicPolicy);
        map.put(new ActiveMQQueue(">"), queuePolicy);

        return activeMQConnectionFactory;
    }


    @Bean
    @Qualifier("ibmConnectionFactory")
    MQQueueConnectionFactory jmsListenerContainerFactory() {
        MQQueueConnectionFactory websphereConnectionFactory = new MQQueueConnectionFactory();
        try {
            websphereConnectionFactory.setQueueManager("admin1");
            websphereConnectionFactory.setHostName("localhost");
            websphereConnectionFactory.setPort(1414);
            websphereConnectionFactory.setTransportType(1);
            websphereConnectionFactory.setChannel("CHANNEL1");
        }
        catch (JMSException ex) {

        }
        return websphereConnectionFactory;
    }

    @Bean
    @Qualifier("ibmConsumer")
    Consumer ibmConsumer () {
        return new Consumer("ibmConsumer");
    }

    @Bean
    @Qualifier("ibmInputListener")
    DefaultMessageListenerContainer ibmInputListener(@Qualifier("ibmConnectionFactory")
                           MQQueueConnectionFactory jmsListenerContainerFactory) {

        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(jmsListenerContainerFactory);
        listener.setDestinationName(Names.IBMInput);
        listener.setSessionTransacted(true);
        listener.setMessageListener(applicationContext.getBean("ibmConsumer"));
        listener.start();
        return listener;
    }

    @Bean
    @Qualifier("ibmOutputListener")
    DefaultMessageListenerContainer ibmOutputListener(@Qualifier("ibmConnectionFactory")
                                                        MQQueueConnectionFactory jmsListenerContainerFactory) {

        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(jmsListenerContainerFactory);
        listener.setDestinationName(Names.IBMOutput);
        listener.setSessionTransacted(true);
        listener.setMessageListener(applicationContext.getBean("ibmConsumer"));
        listener.start();
        return listener;
    }

    @Bean
    @Qualifier("activeMqFactory")
    JmsListenerContainerFactory<?> jmsContainerFactory( @Qualifier("activeMQConnectionFactory")
                                                                ActiveMQConnectionFactory factory) {
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
    DefaultMessageListenerContainer aListener(@Qualifier("activeMQConnectionFactory")
                                                      ActiveMQConnectionFactory factory) {

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
    DefaultMessageListenerContainer bListener(@Qualifier("activeMQConnectionFactory")
                                                      ActiveMQConnectionFactory factory) {

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
    SimpleMessageListenerContainer durableListener(@Qualifier("activeMQConnectionFactory")
                                                           ActiveMQConnectionFactory factory) {
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
    SimpleMessageListenerContainer simpleListener(@Qualifier("activeMQConnectionFactory")
                                                          ActiveMQConnectionFactory factory) {
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
    Producer topicProducer(@Qualifier("activeMQConnectionFactory")
                                   ActiveMQConnectionFactory connectionFactory) {
        JmsTemplate topicTemplate = new JmsTemplate(connectionFactory);
        topicTemplate.setSessionTransacted(true);
        topicTemplate.setPubSubDomain(true);
        JmsMessagingTemplate topicMessagingTemplate =
                new JmsMessagingTemplate(topicTemplate);
        return new Producer(Names.TopicName, topicMessagingTemplate);
    }

    @Bean
    @Qualifier("queueTemplate")
    JmsMessagingTemplate queueTemplate(@Qualifier("activeMQConnectionFactory")
                                               ActiveMQConnectionFactory connectionFactory) {
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
        return new RedirectConsumer("A Consumer",
                (Producer)applicationContext.getBean("bProducer"));
    }

}
