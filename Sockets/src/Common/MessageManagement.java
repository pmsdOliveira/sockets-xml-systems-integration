/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import org.netbeans.xml.schema.updateschema.ObjectFactory;
import org.netbeans.xml.schema.updateschema.TMyPlace;

/**
 *
 * @author André & Ricardo
 */
public class MessageManagement {

    public static String createPlaceStateContent(TMyPlace myPlace) throws JAXBException {
        //TODO Lab 2:
        //Serealize TMyPlace object to String using JAXB        
        JAXBContext context = JAXBContext.newInstance(myPlace.getClass());
        JAXBElement<TMyPlace> element = new ObjectFactory().createMyPlace(myPlace);

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, writer);

        return writer.toString();
    }

    public static TMyPlace retrievePlaceStateObject(String content) throws JAXBException {
        //TODO Lab 2:
        //Deserealize a String to a TMyPlace object using JAXB
        JAXBContext context = JAXBContext.newInstance(TMyPlace.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        TMyPlace myPlace = (TMyPlace) unmarshaller.unmarshal(new StringReader(content));

        return myPlace;
    }
}