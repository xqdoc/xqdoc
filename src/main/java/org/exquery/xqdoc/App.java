package org.exquery.xqdoc;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
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
 * Hello world!
 *
 */
public class App 
{
    public static String getStringFromDoc(org.w3c.dom.Document doc)    {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        LSOutput lsOutput =  domImplementation.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(doc, lsOutput);
        String result = stringWriter.toString();

        return result;
    }

    public static void main( String[] args ) throws ParserConfigurationException, IOException, SAXException {
        ANTLRInputStream inputStream = new ANTLRInputStream("\n" +
                "module namespace  functx = \"http://www.functx.com\" ;\n" +
                "(:~\n" +
                " : Whether a value is all whitespace or a zero-length string \n" +
                " :\n" +
                " : @author  Priscilla Walmsley, Datypic \n" +
                " : @version 1.0 \n" +
                " : @see     http://www.xqueryfunctions.com/xq/functx_all-whitespace.html \n" +
                " : @param   $arg the string (or node) to test \n" +
                " :) \n" +
                "declare function functx:all-whitespace \n" +
                "  ( $arg as xs:string? )  as xs:boolean {\n" +
                "       \n" +
                "   fn:normalize-space($arg) = ''\n" +
                " } ;\n");
        XQueryLexer markupLexer = new XQueryLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        XQueryParser markupParser = new XQueryParser(commonTokenStream);

        XQueryParser.ModuleContext fileContext = markupParser.module();
        StringBuffer buffer = new StringBuffer();
        XQueryVisitor visitor = new XQueryVisitor(buffer);
        visitor.visit(fileContext);
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource isOut = new InputSource();
        isOut.setCharacterStream(new StringReader(buffer.toString()));

        Document doc = db.parse(isOut);
        System.out.println(App.getStringFromDoc(doc));
    }
}
