package activemqtest.services;

import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by zotova on 07.07.2016.
 */
@Component
public class XmlMarshaller<T> {

    private static final String SCHEMA_NAME = "Message.xsd";
    Resource schema;
    Jaxb2Marshaller marshaller;

    public XmlMarshaller(Resource schema, Class objectClass) {
        this.schema = schema;
        marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(objectClass);
        marshaller.setSchema(schema);
    }

    public String toXml(T obj) {

        String xml = null;
        try {
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);
            marshaller.marshal(obj, result);
            xml = outWriter.getBuffer().toString();
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return xml;
    }

    public T fromXml(String xml) {

        T m = null;
        try {
            m = (T) marshaller.unmarshal(new StreamSource(new StringReader(xml)));
        }

        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return m;
    }
}
