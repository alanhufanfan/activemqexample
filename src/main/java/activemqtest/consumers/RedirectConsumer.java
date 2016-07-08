package activemqtest.consumers;

import activemqtest.producers.Producer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zotova on 08.07.2016.
 */
public class RedirectConsumer extends Consumer {


    public RedirectConsumer() {

    }

    public RedirectConsumer(String name, Producer next) {
        super(name);
        nextProducer = next;
    }
}
