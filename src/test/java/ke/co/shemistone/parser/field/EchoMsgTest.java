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
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author bmg
 */
public class EchoMsgTest {

    @Test
    public void testEncode() {
        Logger logger = Logger.getLogger(FixedNumFieldTest.class);
        logger.info("Encoding Network Message ...");
        try {
            byte[] bytes;
            String lines;
            bytes = Files.readAllBytes(Paths.get(FixedNumFieldTest.class.getResource("/iso_field.xml").toURI()));
            lines = new String(bytes, "ISO-8859-1");
            IsoField isoField = Strings.unmarshal(lines, IsoField.class, "application/xml");
            CompoundField field = new FixedCompoundField(isoField);
            field.setValue(0, "0800");// MTI
            field.setValue(7, "1124210025"); // Date and Time
            field.setValue(11, "254513"); // STAN
            field.setValue(70, "301"); // Management Informatioon Code
            String encodedValue = field.encode();
            logger.infof("Encoded value => %s", encodedValue);
            field.setValue("");
            field.decode(encodedValue);
            Assert.assertEquals("0800", field.getValue(0));
            Assert.assertEquals("1124210025", field.getValue(7));
            Assert.assertEquals("254513", field.getValue(11));
            Assert.assertEquals("301", field.getValue(70));
        } catch (JAXBException | IOException | RuntimeException | URISyntaxException ex) {
            logger.error(ex);
            Assert.fail();
        }

    }

}
