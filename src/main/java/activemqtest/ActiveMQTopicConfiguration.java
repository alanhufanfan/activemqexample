package activemqtest;

import activemqtest.consumers.Consumer;
import activemqtest.producers.Producer;
import activemqtest.utils.Names;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * Created by zotova on 15.07.2016.
 */
@Configuration
public class ActiveMQTopicConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("activeMQConnectionFactory")
    ActiveMQConnectionFactory factory;

    @Bean
    @Qualifier("activeMQDurableConsumer")
    Consumer activeMQDurableConsumer () {
        return new Consumer("activeMQDurableConsumer");
    }

    @Bean
    @Qualifier("activeMQSimpleConsumer")
    Consumer activeMQSimpleConsumer() {
        return new Consumer("activeMQsimpleConsumer");
    }

    @Bean
    @Qualifier("activeMQDurableListener")
    DefaultMessageListenerContainer durableListener() {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setClientId("topicId1");
        dmlc.setPubSubDomain(true);
        dmlc.setDestinationName(Names.TopicName);
        dmlc.setMessageListener(applicationContext.getBean("activeMQDurableConsumer"));
        dmlc.setSessionTransacted(true);
        dmlc.setDurableSubscriptionName("durable sub");
        dmlc.setSubscriptionDurable(true);
        dmlc.start();
        return dmlc;
    }

    @Bean
    @Qualifier("activeMQSimpleListener")
    DefaultMessageListenerContainer simpleListener() {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setPubSubDomain(true);
        dmlc.setClientId("topicId2");
        dmlc.setDestinationName(Names.TopicName);
        dmlc.setMessageListener(applicationContext.getBean("activeMQSimpleConsumer"));
        dmlc.setSessionTransacted(true);
        dmlc.setSubscriptionDurable(false);
        dmlc.start();
        return dmlc;
    }

    @Bean
    @Qualifier("topicProducer")
    Producer topicProducer() {
        JmsTemplate topicTemplate = new JmsTemplate(factory);
        topicTemplate.setSessionTransacted(true);
        topicTemplate.setPubSubDomain(true);
        JmsMessagingTemplate topicMessagingTemplate =
                new JmsMessagingTemplate(topicTemplate);
        return new Producer(Names.TopicName, topicMessagingTemplate);
    }

}
