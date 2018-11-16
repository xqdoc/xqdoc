package org.exquery.xqdoc;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.*;
import org.xmlunit.util.Nodes;
import org.xmlunit.util.Predicate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
@RunWith(Parameterized.class)
public class AppTest
{
    public static String getStringFromDoc(Document doc)    {
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

    @Parameters(name = "{index}: xqDoc({0})={1}")
    public static Iterable<String[]> data() {

        AppTest obj = new AppTest("", "");
        ClassLoader classLoader = obj.getClass().getClassLoader();
        String url = classLoader.getResource("XQuery").getFile();
        File directory = new File(url);

        File[] files = directory.listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        String[][] arrayOfArrays = new String[files.length][];

        for(int i=0; i<files.length; i++)
        {
            if (files[i].isFile()) {
                String filename = files[i].getName();
                String[] parts = filename.split("\\.");
                String file1 = "XQuery/" + filename;
                String file2 = "xqDoc/" + parts[0] + ".xml";
                String[] pair = new String[] { file1, file2 };
                arrayOfArrays[i] = pair;
            }
        }

        return Arrays.asList(arrayOfArrays);
    }

    private String input;
    private String expected;

    public AppTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void test() throws IOException, XQDocException, ParserConfigurationException, SAXException {
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
        ClassLoader classLoader = getClass().getClassLoader();
        String source = classLoader.getResource(input).getFile();
        String target = classLoader.getResource(expected).getFile();
        InputStream is = Files.newInputStream(Paths.get(source));
        ANTLRInputStream inputStream = new ANTLRInputStream(is);
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
        String xqDocXML = AppTest.getStringFromDoc(doc);
        DifferenceEngine diff = new DOMDifferenceEngine();
        diff.addDifferenceListener(new ComparisonListener() {
            public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
                assertTrue("found a difference: " + comparison, false);
            }
        });
        Source test = Input.fromString(xqDocXML).build();
        Source control = Input.fromFile(target).build();
        Diff myDiff = DiffBuilder.compare(control).withTest(test)
                .withNodeFilter(new Predicate<Node>() {
                    @Override
                    public boolean test(Node n) {
                        return !(n instanceof Element &&
                                "date".equals(Nodes.getQName(n).getLocalPart()));
                    }
                })
                .checkForSimilar()
                .ignoreWhitespace()
                .build();
        assertFalse("XML similar " + myDiff.toString(), myDiff.hasDifferences());    }
}
