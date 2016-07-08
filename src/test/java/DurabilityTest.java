import activemqtest.Application;
import activemqtest.domain.Message;
import activemqtest.producers.TopicProducer;
import org.junit.runner.RunWith;
import javax.jms.JMSException;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.test.context.junit4.*;

/**
 * Created by zotova on 06.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class DurabilityTest {

    @Autowired
    private SimpleMessageListenerContainer durableListener;

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void durabilityTest() throws InterruptedException, JMSException {

        try {
            Message m = new Message();
            m.setDestinationQueueName("A");
            m.setMessageId(1);
            m.setMessageText("Hello");

            durableListener.stop();
            System.out.println("Durable listener stopped");
            this.topicProducer.sendMessage(m);
            System.out.println("Starting durable listener...");
            durableListener.start();
            System.out.println("Durable listener started");
        }
        catch (Exception ex) {

        }
        //Thread.sleep(1000L);
        // assertThat(this.outputCapture.toString().contains("Test message")).isTrue();
    }

}
