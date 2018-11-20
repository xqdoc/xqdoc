# xqdoc
An Antlr4 implementation of xqDoc for XQuery

## Building

Run the command: ```mvn package```

### Dependencies

* antlr
* commons-cli

### Results

* target/xqdoc-1.9-jar-with-dependencies.jar
* target/xqdoc-1.9.jar

## Command Line Call

```java -jar xqdoc-1.9-jar-with-dependencies.jar -Dprefix=uri -Dprefix=uri -f filepath```

The prefix/uri combination is for the prefixes that are not needed in an import module namespace for the implementation.

e.g.  ```-Dfn=http://www.w3.org/2003/05/xpath-functions``` 
is for the default XPath function library.  This prefix/namespace is included by default.

The *filepath* is the path name to the file with the XQuery source.

## Calling from java

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
