package activemqtest;

import activemqtest.consumers.Consumer;
import activemqtest.producers.Producer;
import activemqtest.utils.Names;
import com.ibm.mq.jms.MQQueueConnectionFactory;
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
public class WebsphereMQTopicConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    @Qualifier("ibmConnectionFactory")
    MQQueueConnectionFactory factory;

    @Bean
    Consumer ibmDurableConsumer() {
        return new Consumer("ibmDurableConsumer");
    }

    @Bean
    Consumer ibmSimpleConsumer() {
        return new Consumer("ibmSimpleConsumer");
    }

    @Bean
    @Qualifier("ibmDurableConsumerListener")
    DefaultMessageListenerContainer ibmDurableConsumerTemplate()
    {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
        dmlc.setClientId("ibmTopicId1");
        dmlc.setPubSubDomain(true);
        dmlc.setDestinationName(Names.IBMTopicName);
        dmlc.setMessageListener(applicationContext.getBean("ibmDurableConsumer"));
        dmlc.setSessionTransacted(true);
        dmlc.setDurableSubscriptionName("durable sub");
        dmlc.setSubscriptionDurable(true);
        dmlc.start();
        return dmlc;
    }

    @Bean
    @Qualifier("ibmSimpleConsumerListener")
    DefaultMessageListenerContainer ibmSimpleConsumerListener()
    {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(factory);
      //  dmlc.setClientId("ibmTopicId1");
        dmlc.setPubSubDomain(false);
        dmlc.setDestinationName(Names.IBMTopicName);
        dmlc.setMessageListener(applicationContext.getBean("ibmSimpleConsumer"));
        dmlc.setSessionTransacted(true);
       // dmlc.setDurableSubscriptionName("durable sub");
        dmlc.setSubscriptionDurable(false);
        dmlc.start();
        return dmlc;
    }

    @Bean
    @Qualifier("ibmTopicProducer")
    Producer ibmTopicProducer() {
        JmsTemplate topicTemplate = new JmsTemplate(factory);
        topicTemplate.setSessionTransacted(true);
        topicTemplate.setPubSubDomain(true);
        JmsMessagingTemplate topicMessagingTemplate =
                new JmsMessagingTemplate(topicTemplate);
        return new Producer(Names.IBMTopicName, topicMessagingTemplate);
    }

}
