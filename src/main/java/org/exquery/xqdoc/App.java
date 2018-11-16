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
import java.util.HashMap;

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
                "declare variable $functx:foo as xs:string := \"foo\";\n\n" +
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

        HashMap uriMap = new HashMap();
        uriMap.put("fn", "http://www.w3.org/2003/05/xpath-functions");
        uriMap.put("cts", "http://marklogic.com/cts"); // MarkLogic Server search functions (Core Text Services)
        uriMap.put("dav", "DAV:"); // Used with WebDAV
        uriMap.put("dbg", "http://marklogic.com/xdmp/debug"); // Debug Built-In functions
        uriMap.put("dir", "http://marklogic.com/xdmp/directory"); // MarkLogic Server directory XML
        uriMap.put("err", "http://www.w3.org/2005/xqt-errors"); // namespace for XQuery and XPath errors
        uriMap.put("error", "http://marklogic.com/xdmp/error"); // MarkLogic Server error namespace
        uriMap.put("local", "http://www.w3.org/2005/xquery-local-functions"); // local namespace for functions defined in main modules
        uriMap.put("lock", "http://marklogic.com/xdmp/lock"); // MarkLogic Server locks
        uriMap.put("map", "http://marklogic.com/xdmp/map"); // MarkLogic Server maps
        uriMap.put("math", "http://marklogic.com/xdmp/math"); // math Built-In functions
        uriMap.put("prof", "http://marklogic.com/xdmp/profile"); // profile Built-In functions
        uriMap.put("prop", "http://marklogic.com/xdmp/property"); // MarkLogic Server properties
        uriMap.put("sec", "http://marklogic.com/xdmp/security"); // security Built-In functions
        uriMap.put("sem", "http://marklogic.com/semantics"); // semantic Built-In functions
        uriMap.put("spell", "http://marklogic.com/xdmp/spell"); // spelling correction functions
        uriMap.put("xdmp", "http://marklogic.com/xdmp"); // MarkLogic Server Built-In functions
        uriMap.put("xml", "http://www.w3.org/XML/1998/namespace"); // XML namespace
        uriMap.put("xmlns", "http://www.w3.org/2000/xmlns/"); // xmlns namespace
        uriMap.put("xqe", "http://marklogic.com/xqe"); // deprecated MarkLogic Server xqe namespace
        uriMap.put("xqterr", "http://www.w3.org/2005/xqt-errors"); // XQuery test suite errors (same as err)
        uriMap.put("xs", "http://www.w3.org/2001/XMLSchema"); // XML Schema namespace

        XQueryVisitor visitor = new XQueryVisitor(buffer, uriMap);
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
