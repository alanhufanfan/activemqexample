package activemqtest;

import activemqtest.domain.Message;
import activemqtest.services.XmlMarshaller;
import activemqtest.services.XmlValidator;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Created by zotova on 15.07.2016.
 */
@Configuration
public class AppConfig {

    private static final String MESSAGE_VALIDATION_SCHEMA = "Message.xsd";

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    XmlValidator xmlValidator() throws Exception {

       // Resource schema = applicationContext.getResource(MESSAGE_VALIDATION_SCHEMA);
        Resource schema = applicationContext.getResource("classpath:Message.xsd");
        return new XmlValidator(schema);
    }

    @Bean
    XmlMarshaller xmlMarshaller() throws Exception {
        Resource schema = applicationContext.getResource(MESSAGE_VALIDATION_SCHEMA);
        return new XmlMarshaller<Message>(schema, Message.class);
    }


}
