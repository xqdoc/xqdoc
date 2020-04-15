package org.xqdoc;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>DocumentUtility class.</p>
 *
 * @author lcahlander
 * @version $Id: $Id
 */
public class DocumentUtility
{
    private DocumentUtility() {

    }

    /**
     * <p>getStringFromDoc.</p>
     *
     * @param doc The XML Document that is to be returned as a String
     * @return The XML Document as a String
     */
    public static String getStringFromDoc(Document doc)    {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        LSOutput lsOutput =  domImplementation.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(doc, lsOutput);
        return stringWriter.toString();
    }

    /**
     * Generate an XML Document from a StringBuffer containing the XML as a String
     *
     * @param buffer The StringBuffer used to build the XML String
     * @return The XML Document object
     * @throws javax.xml.parsers.ParserConfigurationException a
     * @throws java.io.IOException a
     * @throws org.xml.sax.SAXException a
     */
    public static Document getDocumentFromBuffer(StringBuilder buffer) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource isOut = new InputSource();
        isOut.setCharacterStream(new StringReader(buffer.toString()));

        return db.parse(isOut);

    }
}
