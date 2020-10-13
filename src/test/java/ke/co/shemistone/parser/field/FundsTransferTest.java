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
import ke.co.shemistone.parser.Converters;
import ke.co.shemistone.parser.Strings;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author bmg
 */
public class FundsTransferTest {

    @Test
    public void testEncode() {
        Logger logger = Logger.getLogger(FixedNumFieldTest.class);
        logger.info("Encoding Funds Transfer Message ...");
        try {
            byte[] bytes;
            String lines;
            bytes = Files.readAllBytes(Paths.get(FixedNumFieldTest.class.getResource("/iso_field.xml").toURI()));
            lines = new String(bytes, "ISO-8859-1");
            IsoField isoField = Strings.unmarshal(lines, IsoField.class, "application/xml");
            CompoundField field = new FixedCompoundField(isoField);
            field.setValue(0, "0200"); // MTI - n4
            field.setValue(2, "9001000000672941810"); // PAN - n..19
            field.setValue(3, "010000"); // Processing Code - n6
            field.setValue(4, "5000000"); // Amount, Transaction - n12
            field.setValue(5, "50000"); // Amount, Settlement - n12
            field.setValue(6, "50000"); // Amount, Cardholder Billing - n12
            field.setValue(7, "1124222950"); // Date Time - n10
            field.setValue(11, "869236"); // STAN - n6
            field.setValue(12, "012958"); // Local Transaction Time (hhmmss) - n6
            field.setValue(13, "1125"); // Local Transaction Date (MMDD) - n4
            field.setValue(15, "1124"); // Settlement Date - n4
            field.setValue(18, "6011"); // Merchant Type - n4
            field.setValue(19, "254"); // Acquiring Institution Country Code - n3
            field.setValue(22, "1"); // Point of Serice Entry Mode - n3
            field.setValue(25, "00"); // Point of Service Condition Code - n2
            field.setValue(26, "12"); // Point of Service Capture Code - n2
            field.setValue(28, "D00000000"); // Amount, Transaction Fee - x+n8
            field.setValue(32, "666767"); // Acquiring Institution Identificatio Code - n..11
            field.setValue(33, "555555"); // Forwarding Institution Identification Code - n..11
            field.setValue(37, "732822869236"); // Retrieval Reference Number - an12
            field.setValue(41, "42810486"); // Card Acceptor Teerminal Identification - ans8
            field.setValue(42, "Shemistone PLC"); // Card Acceptor Identification Code - ans15
            field.setValue(43, "Shemistone PLC NRB KE"); // Card acceptor name/location (1-23 street address, 24-36 city, 37-38 state, 39-40 country)  - ans40
            field.setValue(49, "835"); // Currency Code Transaction - a3 or n3
            field.setValue(50, "835"); // Currency Setttlement - a3 or n3
            field.setValue(51, "835"); // Currency Cardholder Billing - a3 or n3
            field.setValue(52, Converters.hexToBin("931EFFFFFFFFFFFF")); // PIN Data - b8
            field.setValue(53, "2001000000000000"); // Security Related Control Information - n16
            field.setValue(60, "00000000000000000000"); // Recerved - ans...999
            String encodedValue = field.encode();
            logger.infof("Encoded value => %s", encodedValue);
            field.setValue("");
            field.decode(encodedValue);
            Assertions.assertEquals("0200", field.getValue(0));
            Assertions.assertEquals("0111111000111010011001001101000110001000111000001111100000010000", field.getValue(1));
            Assertions.assertEquals("09001000000672941810", field.getValue(2));
            Assertions.assertEquals("010000", field.getValue(3));
            Assertions.assertEquals("000005000000", field.getValue(4));
            Assertions.assertEquals("000000050000", field.getValue(5));
            Assertions.assertEquals("000000050000", field.getValue(6));
            Assertions.assertEquals("1124222950", field.getValue(7));
            Assertions.assertEquals("869236", field.getValue(11));
            Assertions.assertEquals("012958", field.getValue(12));
            Assertions.assertEquals("1125", field.getValue(13));
            Assertions.assertEquals("1124", field.getValue(15));
            Assertions.assertEquals("6011", field.getValue(18));
            Assertions.assertEquals("0254", field.getValue(19));
            Assertions.assertEquals("0001", field.getValue(22));
            Assertions.assertEquals("00", field.getValue(25));
            Assertions.assertEquals("12", field.getValue(26));
            Assertions.assertEquals("0D00000000", field.getValue(28));
            Assertions.assertEquals("666767", field.getValue(32));
            Assertions.assertEquals("555555", field.getValue(33));
            Assertions.assertEquals("732822869236", field.getValue(37));
            Assertions.assertEquals("42810486", field.getValue(41));
            Assertions.assertEquals("Shemistone PLC ", field.getValue(42));
            Assertions.assertEquals("Shemistone PLC NRB KE                   ", field.getValue(43));
            Assertions.assertEquals("0835", field.getValue(49));
            Assertions.assertEquals("0835", field.getValue(50));
            Assertions.assertEquals("0835", field.getValue(51));
            Assertions.assertEquals(field.getValue(52), Converters.hexToBin("931EFFFFFFFFFFFF"));
            Assertions.assertEquals("2001000000000000", field.getValue(53));
            Assertions.assertEquals("00000000000000000000", field.getValue(60));
        } catch (JAXBException | IOException | RuntimeException | URISyntaxException ex) {
            logger.error(ex);
            Assertions.fail();
        }

    }

}
