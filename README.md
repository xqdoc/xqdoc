# xqDoc
An Antlr4 implementation of xqDoc for XQuery

## Comments

xqDoc comments are used to document XQuery library and main modules in a manner similar to how Javadoc comments are used to document Java classes and packages. With the documentation close to the source, it increases the chances that the documentation will be kept current and with tools provided by xqDoc, useful documentation can be quickly and easily generated. It should be noted that a XQuery module does not need to contain xqDoc style comments in order for the xqDoc tools to produce useful output. Without any xqDoc documentation style comments, a very useful cross reference (for control, modules, imports, variables and functions) and XQuery code browser (for modules and functions) will be created by the xqDoc tools.

xqDoc style comments begin with a  '(:~'  and end with a  ':)' . Of course,  '(::'  would have been preferable to indicate the beginning of an xqDoc style comment (since it mimics the JavaDoc style of  '/**' ) but we didn't want to cause confusion with an XQuery pragma (since this decision was made, the definition for XQuery pragma has been changed). The choice for the begin pattern is really quite arbitrary, and we're open to suggestions. In any case, one xqDoc style comment can be specified before each of the following rules (based on the W3C XQuery 1.0 BNF) for library modules and main modules.

Library Modules

* ModuleDecl
* ModuleImport
* VarDecl
* FunctionDecl

Main Modules

* MainModule
* ModuleImport
* VarDecl
* FunctionDecl

Like Javadoc, the following tags have special meaning within an xqDoc comment. In addition, the values provided for each of the tags can contain embedded XHTML markup to enhance or emphasize the xqDoc XHTML presentation. However, make sure the content is well formed and that entities are used (i.e. &amp; instead of &). The beginning text (up to the first tag) is assumed to be description text for the component being documented.  

All tags allow for multiple lines for that tag.

For example:

```
@custom:openapi
{
  "servers": [
    {
      "url": "https://{username}.gigantic-server.com:{port}/{basePath}",
      "description": "The production API server",
      "variables": {
        "username": {
          "default": "demo",
          "description": "this value is assigned by the service provider, in this example `gigantic-server.com`"
        },
        "port": {
          "enum": [
            "8443",
            "443"
          ],
          "default": "8443"
        },
        "basePath": {
          "default": "v2"
        }
      }
    }
  ]
}
```

becomes

```
<xqdoc:custom tag="openapi">
 {
   "servers": [
     {
       "url": "https://{username}.gigantic-server.com:{port}/{basePath}",
       "description": "The production API server",
       "variables": {
         "username": {
           "default": "demo",
           "description": "this value is assigned by the service provider, in this example `gigantic-server.com`"
         },
         "port": {
           "enum": [
             "8443",
             "443"
           ],
           "default": "8443"
         },
         "basePath": {
           "default": "v2"
         }
       }
     }
   ]
  }</xqdoc:custom>

```

### @author

The @author tag identifies the author for the documented component. Zero or more @author tags can be specified (one per author)

```@author Darin McBeath```

### @version

The @version tag identifies the version of the documented component. Zero or more @version tags can be specified (one per version) but in reality only a single @version tag would normally make sense. The value for the @version tag can be an arbitrary string.

```@version 1.0```

### @since

The @since tag identifies the version when a documented component was supported. Zero or many @since tags can be specified, but in reality only a single @since tag would normally make sense. The value for the @since tag can be an arbitrary string but should likely match an appropriate version value.

```@since 1.0```

### @see

The @see tag provides the ability to hypertext link to an external web site, a library or main module contained in xqDoc, a specific function (or variable) defined in a library or main module contained in xqDoc, or arbitrary text. To link to an external site, use a complete URL such as http://www.xquery.com. To link to a library or main module contained in xqDoc, simply provide the URI for the library or main module. To link to a specific function (or variable) defined in an xqDoc library or main module, simply provide the URI for the library or main module followed by a ';' and finally the function or variable name. To provide a name for a link, simply include a second ';' followed by the name. To provide text, simply include the 'text'. Multiple @see tags can be specified (one per link or string of text).

```angular2
@see http://www.xquery.com
@see xqdoc/xqdoc-display
@see xqdoc/xqdoc-display;build-link
@see xqdoc/xqdoc-display;$months
@see xqdoc/xqdoc-display;$months;month variable
@see http://www.xquery.com;;xquery
@see some text
```

### @param

The @param tag identifies the parameters associated with a function. For each parameter in a function, there should be a @param tag. The @param tag should be followed by the parameter name (as indicated in the function signature) and then the parameter description.

```@param $name The username```

### @return

The @return tag describes what is returned from a function. Zero or one @return tags can be specified.

```@return Sequence of names matching the search criteria```

### @deprecated

The @deprecated tag identifies the identifies the documented component as being deprecated. The string of text associated with the @deprecated tag should indicate when the item was deprecated and what to use as a replacement.

```@deprecated As of 1.0 and replaced with add-user```

### @error

The @error tag identifies the types of errors that can be generated by the function. Zero or more @error tags can be specified. An arbitrary string of text can be provided for a value.

```@error The requested URI does not exist```

### @custom

The @custom tag identifies a tag for any other purpose.  If the @custom is followed immediately by a colon, then that value is in the tag attribute of the custom tag.  e.g.  ```@custom:openapi``` creates the tag ```<xqdoc:custom tag="openapi">```

## Examples

A representative library module xqDoc comment is included below. This comment would precede the module declaration statement for the library module.

```
(:~ 
: This module provides the functions that control the Web presentation
: of xqDoc. The logic contained in this module is not specific to any
: XQuery implementation and is written to the May 2003 XQuery working
: draft specification. It would be a trivial exercise to convert this
: code to either the Nov 2003 or Oct 2004 XQuery working draft.
:
: It should also be noted that these functions not only support the 
: real-time presentation of the xqDoc information but are also used
: for the static offline presentation mode as well. The static offline
: presentation mode has advantages because access to a native XML
: database is not needed when viewing the xqDoc information ... it is
: only needed when generating the offline materials. 
:
: @author Darin McBeath
: @version 1.0
:)
module namespace display="xqdoc/display";
```

A representative library module xqDoc function comment is included below. This comment would precede the function declaration statement in the library module.

```
(:~ 
: The controller for constructing the xqDoc HTML information for
: the specified library module. The following information for
: each library module will be generated.
: <ul>
: <li> Module introductory information</li>
: <li> Global variables declared in this module</li>
: <li> Modules imported by this module</li>
: <li> Summary information for each function defined in the module</li>
: <li> Detailed information for each function defined in the module</li>
: </ul>
:
: @param $uri the URI for the library module
: @param $local indicates whether to build static HTML link for offline
: viewing or dynamic links for real-time viewing.
: @return XHTML.
:)
define function display:print-module($uri as xs:string, $local as xs:boolean) as element()*
```

## Building

Run the command: ```mvn package```

### Dependencies

* antlr
* commons-cli

### Results

* target/xqdoc-1.9-jar-with-dependencies.jar
* target/xqdoc-1.9.jar

## Command Line Call

```java -jar xqdoc-1.9-8-jar-with-dependencies.jar -Dprefix=uri -Dprefix=uri -f filepath```

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

## Running from MarkLogic's ml-gradle

Here is the entry for ```build.gradle``` to add the tasks for the generation of the xqDoc from within an ml-gradle project.

```
import org.apache.tools.ant.filters.BaseFilterReader

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath "org.xqdoc:xqdoc:1.9.9.1"
    }
}

plugins {
  id "net.saliman.properties" version "1.4.6"
  id "com.marklogic.ml-gradle" version "3.6.0"
}

repositories {
  jcenter()
  maven { url "http://developer.marklogic.com/maven2/" }
  maven { url "http://repository.cloudera.com/artifactory/cloudera-repos/" }
}

configurations {
  mlcp {
    resolutionStrategy {
      force "xml-apis:xml-apis:1.4.01"
    }
  }
}

dependencies {
    mlcp "com.marklogic:mlcp:9.0.6"
    mlcp files("marklogic/lib")
}

class XQDocFilter extends BaseFilterReader {
    XQDocFilter(Reader input) {
        super(new StringReader(new org.xqdoc.MarkLogicProcessor().process(input.text)))
    }
}

task generateXQDocs(type: Copy) {
  into 'xqDoc'
  from 'src/main/ml-modules/root'
  include '**/*.xqy'
  rename { it - '.xqy' + '.xml' } 
  includeEmptyDirs = false
  filter XQDocFilter
}

 task importXQDoc(type: com.marklogic.gradle.task.MlcpTask) {
  classpath = configurations.mlcp
  command = "IMPORT"
  database = "emh-accelerator-content"
  input_file_path = "xqDoc"
  output_collections = "xqdoc"
  output_uri_replace = ".*xqDoc,'/xqDoc'"
  document_type = "mixed"
}


```

## Display of xqDoc in a MarkLogic application

There is a GitHub project to display the xqDoc within a MarkLogic project.  It is available at [marklogic-xqdoc-display](https://github.com/lcahlander/marklogic-xqdoc-display)
