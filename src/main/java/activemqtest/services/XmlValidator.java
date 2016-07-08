package activemqtest.services;

import activemqtest.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;

/**
 * Created by zotova on 07.07.2016.
 */
@Component
public class XmlValidator {

    Validator validator;
    Resource schema;

    public XmlValidator(Resource schema) throws IOException, SAXException, NullPointerException {

        this.schema = schema;
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        validator = factory.newSchema(schema.getFile()).newValidator();
    }

    public boolean validate(String xml)  {
        Source source = new StreamSource(new StringReader(xml));

        boolean isValid = true;

        try {
            validator.validate(source);
        }
        catch (Exception e) {
            //System.err.println("Not valid");
            isValid = false;
        }

        return isValid;
    }
}
