package org.exquery.xqdoc;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

public class ExistDBProcessor
{

    public String process(String txt) throws XQDocException, ParserConfigurationException, IOException, SAXException {
        HashMap uriMap = new HashMap();
        uriMap.put("lucene", "http://exist-db.org/xquery/lucene");
        uriMap.put("ngram", "http://exist-db.org/xquery/ngram");
        uriMap.put("sort", "http://exist-db.org/xquery/sort");
        uriMap.put("range", "http://exist-db.org/xquery/range");
        uriMap.put("spatial", "http://exist-db.org/xquery/spatial");
        uriMap.put("inspection", "http://exist-db.org/xquery/inspection");
        uriMap.put("mail", "http://exist-db.org/xquery/mail");
        uriMap.put("math", "http://exist-db.org/xquery/math");
        uriMap.put("request", "http://exist-db.org/xquery/request");
        uriMap.put("response", "http://exist-db.org/xquery/response");
        uriMap.put("sm", "http://exist-db.org/xquery/securitymanager");
        uriMap.put("session", "http://exist-db.org/xquery/session");
        uriMap.put("system", "http://exist-db.org/xquery/system");
        uriMap.put("transform", "http://exist-db.org/xquery/transform");
        uriMap.put("util", "http://exist-db.org/xquery/util");
        uriMap.put("validation", "http://exist-db.org/xquery/validation");
        uriMap.put("xmldb", "http://exist-db.org/xquery/xmldb");
        uriMap.put("map", "http://www.w3.org/2005/xpath-functions/map");
        uriMap.put("math", "http://www.w3.org/2005/xpath-functions/math");
        uriMap.put("array", "http://www.w3.org/2005/xpath-functions/array");
        uriMap.put("process", "http://exist-db.org/xquery/process");
        uriMap.put("xs", "http://www.w3.org/2001/XMLSchema"); // XML Schema namespace
        ANTLRInputStream inputStream = new ANTLRInputStream(txt);
        XQueryLexer markupLexer = new XQueryLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(markupLexer);
        XQueryParser markupParser = new XQueryParser(commonTokenStream);

        XQueryParser.ModuleContext fileContext = markupParser.module();
        StringBuffer buffer = new StringBuffer();


        XQueryVisitor visitor = new XQueryVisitor(buffer, uriMap);
        visitor.visit(fileContext);
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource isOut = new InputSource();
        isOut.setCharacterStream(new StringReader(buffer.toString()));

        Document doc = db.parse(isOut);
        return DocumentUtility.getStringFromDoc(doc);
    }
}
