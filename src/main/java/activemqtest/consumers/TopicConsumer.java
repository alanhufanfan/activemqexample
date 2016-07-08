package activemqtest.consumers;

import activemqtest.services.XmlValidator;
import activemqtest.utils.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Created by zotova on 07.07.2016.
 */

public class TopicConsumer implements MessageListener {

    protected String name;

    @Autowired
    XmlValidator validator;

    public TopicConsumer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void onMessage(Message message ) {
        StringBuilder b = new StringBuilder();
        b.append("------------------- ");
        b.append(Names.TopicName);
        b.append(" ");
        b.append(name);
        b.append(" got message ");
        b.append("-------------------");
        b.append("\n");
        String isValid = validator.validate(message.toString()) ? message.toString() :
                "Message in " + Names.TopicName + "consumer is invalid";
        b.append(isValid);
        System.out.println(b.toString());
    }
}
