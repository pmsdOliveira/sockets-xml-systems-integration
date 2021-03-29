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
import org.netbeans.xml.schema.updateschema.ObjectFactory;
import org.netbeans.xml.schema.updateschema.TMyPlace;

/**
 *
 * @author André & Ricardo
 */
public class MessageManagement {

    public static String createPlaceStateContent(TMyPlace myPlace) throws JAXBException {
        //TODO - Copy your code from Lab 2 here
        //Serealize TMyPlace object to String using JAXB

        return null;
    }

    public static TMyPlace retrievePlaceStateObject(String content) throws JAXBException {
        //TODO - Copy your code from Lab 2 here
        //Deserealize a String to a TMyPlace object using JAXB
        
        return null;
    }
}
