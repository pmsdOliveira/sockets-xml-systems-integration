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
import javax.xml.namespace.QName;
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
        Class tMyPlaceClass = org.netbeans.xml.schema.updateschema.TMyPlace.class;
        
        JAXBContext context = JAXBContext.newInstance(tMyPlaceClass);
        JAXBElement<org.netbeans.xml.schema.updateschema.TMyPlace> element = new JAXBElement(new QName("MyPlace"), tMyPlaceClass, myPlace);
        
        return null;
    }

    public static TMyPlace retrievePlaceStateObject(String content) throws JAXBException {
        //TODO Lab 2:
        //Deserealize a String to a TMyPlace object using JAXB

        return null;
    }
}
