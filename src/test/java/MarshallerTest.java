import activemqtest.Application;
import activemqtest.domain.Message;
import activemqtest.services.XmlMarshaller;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import java.util.*;
/**
 * Created by zotova on 07.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class MarshallerTest extends TestCase {

    @Autowired
    XmlMarshaller marshaller;

    private MessageFactory factory = new MessageFactory();

    class MessageFactory {
        SecureRandom secureRandom;
        Random random;
        String ln = "abcdefghi jklmnopqrstuvwxyz";
        int idIncrement = 0;
        int idMax = 65534;
        int minLen = 10;
        int maxLen = 200;

        MessageFactory() {
            secureRandom = new SecureRandom();
            random = new Random();
        }

        private String nextMessageText(int len) {
            if (len > maxLen)
                len = maxLen;
            if (len < minLen)
                len = minLen;

            StringBuilder b = new StringBuilder();

            for (int i = 0; i < len; i++) {
                b.append(ln.charAt(random.nextInt(ln.length() - 1)));
            }
            return b.toString();
        }
        private String nextDestinationQueueName() {
            return new BigInteger(130, random).toString(32);
        }
        public Message getInstance() {

            Message m = new Message();
            m.setMessageId(idIncrement);
            m.setDestinationQueueName(nextDestinationQueueName());
            m.setMessageText(nextMessageText(random.nextInt(maxLen)));
            idIncrement ++;
            return m;
        }
    }

    private boolean messageEquals(Message first, Message second) {
       return first.getDestinationQueueName().equals(second.getDestinationQueueName())
                && first.getMessageId() == second.getMessageId() &&
                first.getMessageText().equals(second.getMessageText());
    }

    @Test
    public void testMarshalToXml() {

        Message m = new Message();
        m.setMessageText("Hello");
        m.setMessageId(1);
        m.setDestinationQueueName("example");

        StringBuilder expected = new StringBuilder();
        expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        expected.append("<Message>");
        expected.append("<messageId>1</messageId>");
        expected.append("<destinationQueueName>example</destinationQueueName>");
        expected.append("<messageText>Hello</messageText>");
        expected.append("</Message>");

        assertEquals(expected.toString(), marshaller.toXml(m));

    }

    @Test
    public void testMarshalToXmlNull() {

        assertEquals(null, marshaller.toXml(null));
    }

    @Test
    public void testMarshalFromXml() {

        StringBuilder expected = new StringBuilder();
        expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        expected.append("<Message>");
        expected.append("<messageId>1</messageId>");
        expected.append("<destinationQueueName>example</destinationQueueName>");
        expected.append("<messageText>Hello</messageText>");
        expected.append("</Message>");

        Message m = new Message();
        m.setMessageText("Hello");
        m.setMessageId(1);
        m.setDestinationQueueName("example");

        Message fromXml = (Message)marshaller.fromXml(expected.toString());

        assertTrue(messageEquals(fromXml, m));

    }

    @Test
    public void testMarshalFromXmlWithDifferentValues() {
        StringBuilder expected = new StringBuilder();
        expected.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        expected.append("<Message>");
        expected.append("<messageId>1</messageId>");
        expected.append("<destinationQueueName>example</destinationQueueName>");
        expected.append("<messageText>Hello</messageText>");
        expected.append("</Message>");

        Message m = factory.getInstance();

        Message fromXml = (Message)marshaller.fromXml(expected.toString());

        assertFalse(messageEquals(fromXml, m));

    }

    @Test
    public void testMarshalFromXmlInvalidString() {

        assertEquals(null, marshaller.fromXml("invalid string"));
    }
}
