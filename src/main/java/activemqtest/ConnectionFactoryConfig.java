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

import static activemqtest.utils.Names.IBMTopicName;

/**
 * Created by zotova on 06.07.2016.
 */
@Configuration
public class ConnectionFactoryConfig {

    @Autowired
    private ApplicationContext applicationContext;

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

   /*
    @Bean
    @Qualifier("activeMqFactory")
    JmsListenerContainerFactory<?> jmsContainerFactory( @Qualifier("activeMQConnectionFactory")
                                                                ActiveMQConnectionFactory factory) {
        SimpleJmsListenerContainerFactory simpleFactory = new SimpleJmsListenerContainerFactory();
        simpleFactory.setConnectionFactory(factory);
     //   simpleFactory.setPubSubDomain(false);
        return simpleFactory;
    }

*/

}
