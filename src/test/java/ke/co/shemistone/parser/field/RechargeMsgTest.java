/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.shemistone.parser.field;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.bind.JAXBException;
import ke.co.shemistone.parser.Strings;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author bmg
 */
public class RechargeMsgTest {
    
    @Test
    public void testEncode(){
        Logger logger = Logger.getLogger(FixedNumFieldTest.class);
        logger.info("Encoding Recharge Message ...");
        try {
            byte[] bytes;
            String lines;
            bytes = Files.readAllBytes(Paths.get(FixedNumFieldTest.class.getResource("/iso_field.xml").toURI()));
            lines = new String(bytes, "ISO-8859-1");
            IsoField isoField = Strings.unmarshal(lines, IsoField.class, "application/xml");
            CompoundField field = new FixedCompoundField(isoField);
            field.setValue(0, "0200");// MTI
            field.setValue(7, "1124210025"); // Date and Time
            field.setValue(11, "254513"); // STAN
            field.setValue(4, "200"); // Amount
            field.setValue(127, 2,"254678268023"); // ans12
            field.setValue(127, 3,"8034809899"); // n..18
            String encodedValue = field.encode();
            logger.infof("Encoded value => %s", encodedValue);
            field.setValue("");
            field.decode(encodedValue);
            Assertions.assertEquals("0200", field.getValue(0));
            Assertions.assertEquals("1124210025", field.getValue(7));
            Assertions.assertEquals("254513", field.getValue(11));
            Assertions.assertEquals("000000000200", field.getValue(4));
            Assertions.assertEquals("254678268023", field.getValue(127, 2));
            Assertions.assertEquals("8034809899", field.getValue(127, 3));
        } catch (JAXBException | IOException | RuntimeException | URISyntaxException ex) {
            logger.error(ex);
            Assertions.fail();
        }
        
    }
    
}
