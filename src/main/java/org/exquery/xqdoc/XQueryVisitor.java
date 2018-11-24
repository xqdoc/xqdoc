package org.exquery.xqdoc;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class XQueryVisitor extends XQueryParserBaseVisitor<String> {
    private StringBuffer stream;
    private XQueryParser.XqDocCommentContext xqDocCommentContext = null;
    DateTimeFormatter isoFormat = ISODateTimeFormat.dateTime();

    // HashMap of predefined function namespaces (set by XQDocController via
    // init())
    private HashMap predefinedFunctionNamespaces;

    // Default function namespace (set by XQDocController via init())
    private String defaultFunctionNamespace = "http://www.w3.org/2003/05/xpath-functions";


    // HashMap of prefixes to uris (set by Parser)
    private HashMap uriModuleMap = new HashMap();

    // Default module specified function namespace (set by Parser)
    private String defaultModuleFunctionNamespace;

    // Hash for holding the imported schemas and libraries
    private HashMap<String, ImportDeclaration> imports = new HashMap<String, ImportDeclaration>();

    // Hash for holding the declared namespaces
    private HashMap<String,String> declaredNamespaces = new HashMap<String,String>();

    private StringBuffer declaredVariables = new StringBuffer();

    private StringBuffer declaredFunctions = new StringBuffer();

    // Hash for holding the invoked functions for the current function
    private HashSet<String> invokedFunctions = new HashSet<String>();

    // Hash for holding the referenced variables for the current function
    private HashSet<String> referencedVariables = new HashSet<String>();

    // Flag to indicate whether document URIs should be encoded
    private boolean encodeURIs = false;

    public XQueryVisitor(StringBuffer stream, HashMap uriMap)
    {
        this.stream = stream;
        this.predefinedFunctionNamespaces = uriMap;
    }

    /**
     * Encoded document URIs. Currently, only a '/' is encoded. Some XML
     * databases have problems if a document URI contains a '/'.
     *
     * @param bool
     *            flag to indicate whether URIs should be encoded
     */
    public void setEncodeURIs(boolean bool) {
        encodeURIs = bool;
    }

    /**
     * Return the encode document URI flag.
     *
     * @return the encode flag
     */
    public boolean getEncodeURIs() {
        return encodeURIs;
    }

    private String printXQDocumentation()
    {
        if (xqDocCommentContext != null)
        {
            StringBuffer buffer = new StringBuffer();
            int a = xqDocCommentContext.start.getStartIndex();
            int b = xqDocCommentContext.stop.getStopIndex();
            Interval interval = new Interval(a,b);
            String xqDocBody = xqDocCommentContext.start.getInputStream().getText(interval);
            XQDocComment xqDocComment = new XQDocComment();
            xqDocComment.clear();
            xqDocComment.setComment(xqDocBody);
            String comment = xqDocComment.getXML().toString();
            buffer.append(comment).append("\n");
            xqDocCommentContext = null;
            return buffer.toString();
        }
        return "";
    }

    @Override
    public String visitModule(XQueryParser.ModuleContext context)
    {
        StringBuffer moduleXQDoc = new StringBuffer();
        for (XQueryParser.XqDocCommentContext comment : context.xqDocComment() )
        {
            xqDocCommentContext = comment;
            moduleXQDoc.append(printXQDocumentation());
        }
        stream.append("<xqdoc:xqdoc xmlns:xqdoc=\"http://www.xqdoc.org/1.0\">").append("\n");
        stream.append("<xqdoc:control>").append("\n");
        stream.append("<xqdoc:date>");
        stream.append(new DateTime().toString(isoFormat));
        stream.append("</xqdoc:date>").append("\n");
        stream.append("<xqdoc:version>1.1</xqdoc:version>").append("\n");
        stream.append("</xqdoc:control>").append("\n");
        if (context.libraryModule() != null && context.libraryModule().moduleDecl() != null)
        {
            XQueryParser.ModuleDeclContext moduleDeclContext = context.libraryModule().moduleDecl();
            String prefixText = moduleDeclContext.ncName().getText();
            String uriText = moduleDeclContext.uri.getText();
            String uriTrimText = uriText.substring(1).substring(0, uriText.length() - 2);
            uriModuleMap.put(prefixText, uriTrimText);
            stream.append("<xqdoc:module type=\"library\">").append("\n");
            stream.append("<xqdoc:uri>").append(uriTrimText).append("</xqdoc:uri>").append("\n");
            stream.append("<xqdoc:name>").append(prefixText).append("</xqdoc:name>").append("\n");
            stream.append(moduleXQDoc);
            stream.append(printBody(context));

            stream.append("</xqdoc:module>").append("\n");
            visitChildren(context.libraryModule().prolog());
        }
        if (context.mainModule() != null)
        {
            stream.append("<xqdoc:module type=\"main\">").append("\n");
            stream.append(moduleXQDoc);
            stream.append(printBody(context));
            stream.append("</xqdoc:module>").append("\n");
            visitChildren(context.mainModule());
        }
        buildImports();
        buildNamespaces();
        buildVariables();
        buildFunctions();
        stream.append("</xqdoc:xqdoc>").append("\n");
        return null;
    }

    @Override
    public String visitProlog(XQueryParser.PrologContext context)
    {
        xqDocCommentContext = null;
        return null;
    }

    private void buildImports()
    {
        if (!imports.isEmpty())
        {
            stream.append("<xqdoc:imports>").append("\n");
            for (ImportDeclaration importBody : imports.values())
            {
                stream.append(importBody.toString());
            }
            stream.append("</xqdoc:imports>").append("\n");
        }
    }

    private void buildNamespaces()
    {
        if (!declaredNamespaces.isEmpty())
        {
            stream.append("<xqdoc:namespaces>").append("\n");
            for (Map.Entry<String,String> namespaceEntry : declaredNamespaces.entrySet())
            {
                stream.append("<xqdoc:namespace prefix=\"");
                stream.append(namespaceEntry.getKey());
                stream.append("\" uri=\"");
                stream.append(namespaceEntry.getValue());
                stream.append("\"/>\n");
            }
            stream.append("</xqdoc:namespaces>").append("\n");
        }
    }

    private void buildVariables()
    {
        if (declaredVariables.length() > 0)
        {
            stream.append("<xqdoc:variables>").append("\n");
            stream.append(declaredVariables);
            stream.append("</xqdoc:variables>").append("\n");
        }
    }

    private void buildFunctions()
    {
        if (declaredFunctions.length() > 0)
        {
            stream.append("<xqdoc:functions>").append("\n");
            stream.append(declaredFunctions);
            stream.append("</xqdoc:functions>").append("\n");
        }
    }

    @Override
    public String visitSchemaImport(XQueryParser.SchemaImportContext context)
    {
        String prefix = context.schemaPrefix().ncName().getText();
        String uri = context.nsURI.getText();
        String xqDoc = printXQDocumentation();

        if (!imports.containsKey(prefix))
        {
            imports.put(prefix, new ImportDeclaration(uri, "schema", xqDoc));
        }
        return null;
    }

    @Override
    public String visitModuleImport(XQueryParser.ModuleImportContext context)
    {
        String prefix = context.ncName().getText();
        String uri = context.nsURI.getText();
        String xqDoc = printXQDocumentation();

        if (!imports.containsKey(prefix))
        {
            imports.put(prefix, new ImportDeclaration(uri, "library", xqDoc));
        }
        return null;
    }

    @Override
    public String visitNamespaceDecl(XQueryParser.NamespaceDeclContext context)
    {
        String prefix = context.ncName().getText();
        String uri = context.uriLiteral().getText();

        if (uri.startsWith("\"")) {
            uri = uri.substring(1).substring(0, uri.length() - 2);
        }

        if (!declaredNamespaces.containsKey(prefix))
        {
            declaredNamespaces.put(prefix, uri);
        }
        return null;
    }

    private StringBuffer processAnnotations(XQueryParser.AnnotationsContext annotations)
    {
        StringBuffer buffer = new StringBuffer();
        if (annotations != null && annotations.children != null) {
            buffer.append("<xqdoc:annotations>").append("\n");
            for (XQueryParser.AnnotationContext annotation: annotations.annotation())
            {
                buffer.append("<xqdoc:annotation name=\"");
                buffer.append(annotation.qName().getText());
                buffer.append("\">").append("\n");
                if (annotation.annotList() != null) {
                    for (XQueryParser.AnnotationParamContext annotationParam: annotation.annotList().annotationParam())
                    {
                        buffer.append("<xqdoc:literal>");
                        int a = annotationParam.start.getStartIndex();
                        int b = annotationParam.stop.getStopIndex();
                        Interval interval = new Interval(a,b);
                        buffer.append(annotationParam.start.getInputStream().getText(interval));
                        //buffer.append(annotationParam.literal().getText());
                        buffer.append("</xqdoc:literal>").append("\n");
                    }
                }
                buffer.append("</xqdoc:annotation>").append("\n");
            }
            buffer.append("</xqdoc:annotations>").append("\n");
        }
        return buffer;
    }

    private StringBuffer processTypeDeclaration(XQueryParser.TypeDeclarationContext context)
    {
        StringBuffer buffer = new StringBuffer();
        if (context != null) {
            buffer.append("<xqdoc:type");
            if (context.sequenceType() != null && context.sequenceType().occurrence != null)
            {
                buffer.append(" occurrence=\"");
                buffer.append(context.sequenceType().occurrence.getText());
                buffer.append("\"");
            }
            buffer.append(">");
            buffer.append(context.sequenceType().itemType().getText());
            buffer.append("</xqdoc:type>").append("\n");
        }
        return buffer;
    }

    @Override
    public String visitVarDecl(XQueryParser.VarDeclContext context)
    {
        // Separate the variable name into namspace prefix and localname
        String namespacePrefix = null;
        String namespace = null;
        String localName = null;
        String[] tmp = context.varName().getText().split(":", 2);
        if (tmp.length > 1) {
            namespacePrefix = tmp[0];
            localName = tmp[1];
        } else {
            return null;
        }

        // Get the actual namespace
        if (namespacePrefix == null) {
            if (defaultModuleFunctionNamespace != null) {
                namespace = defaultModuleFunctionNamespace;
            } else if (defaultFunctionNamespace != null) {
                namespace = defaultFunctionNamespace;
            }
        } else {
            namespace = (String) (uriModuleMap.get(namespacePrefix));
            if (namespace == null) {
                namespace = (String) (predefinedFunctionNamespaces
                        .get(namespacePrefix));
            }
        }

        // References a namespace we don't know about
        if (namespace == null)
            return null;

        if (encodeURIs) {
            namespace = encodeURI(namespace);
        }
        declaredVariables.append("<xqdoc:variable>").append("\n");
        declaredVariables.append("<xqdoc:uri>").append(namespace).append("</xqdoc:uri>").append("\n");
        declaredVariables.append("<xqdoc:name>").append(localName).append("</xqdoc:name>").append("\n");
        declaredVariables.append(printXQDocumentation());
        declaredVariables.append(processAnnotations(context.annotations()));
        declaredVariables.append(processTypeDeclaration(context.typeDeclaration()));
        declaredVariables.append("</xqdoc:variable>").append("\n");
        return null;
    }

    @Override
    public String visitFunctionDecl(XQueryParser.FunctionDeclContext context)
    {
        XQueryParser.FunctionParamsContext functionParamsContext = context.functionParams();
        XQueryParser.FunctionReturnContext functionReturnContext = context.functionReturn();
        String functionName = context.name.getText();
        String[] nameParts = functionName.split(":");
        String localName = nameParts[nameParts.length - 1];
        invokedFunctions = new HashSet<String>();
        referencedVariables = new HashSet<String>();

        declaredFunctions.append("<xqdoc:function>").append("\n");
        declaredFunctions.append(printXQDocumentation());
        declaredFunctions.append("<xqdoc:name>");
        declaredFunctions.append(localName);
        declaredFunctions.append("</xqdoc:name>").append("\n");
        declaredFunctions.append(processAnnotations(context.annotations()));
        declaredFunctions.append("<xqdoc:signature>declare function ");
        declaredFunctions.append(localName);
        declaredFunctions.append("(");
        if (functionParamsContext != null)
        {
            int a = functionParamsContext.start.getStartIndex();
            int b = functionParamsContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            declaredFunctions.append(context.start.getInputStream().getText(interval));
        }
        declaredFunctions.append(")");
        if (functionReturnContext != null)
        {
            declaredFunctions.append(" ");
            int a = functionReturnContext.start.getStartIndex();
            int b = functionReturnContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            declaredFunctions.append(context.start.getInputStream().getText(interval));
        }
        declaredFunctions.append("</xqdoc:signature>").append("\n");
        if (functionParamsContext != null)
        {
            declaredFunctions.append("<xqdoc:parameters>").append("\n");
            for (XQueryParser.FunctionParamContext functionParam: functionParamsContext.functionParam())
            {
                declaredFunctions.append("<xqdoc:parameter>").append("\n");
                declaredFunctions.append("<xqdoc:name>");
                declaredFunctions.append(functionParam.name.getText());
                declaredFunctions.append("</xqdoc:name>").append("\n");
                declaredFunctions.append(processTypeDeclaration(functionParam.type));
                declaredFunctions.append("</xqdoc:parameter>").append("\n");
            }
            declaredFunctions.append("</xqdoc:parameters>").append("\n");
        }
        if (functionReturnContext != null)
        {
            declaredFunctions.append("<xqdoc:return>").append("\n");
            declaredFunctions.append("<xqdoc:type");
            if (functionReturnContext.sequenceType().occurrence != null)
            {
                declaredFunctions.append(" occurrence=\"");
                declaredFunctions.append(functionReturnContext.sequenceType().occurrence.getText());
                declaredFunctions.append("\"");
            }
            declaredFunctions.append(">");
            if (functionReturnContext.sequenceType().itemType() != null)
            {
                declaredFunctions.append(functionReturnContext.sequenceType().itemType().getText());
            }
            declaredFunctions.append("</xqdoc:type>").append("\n");
            declaredFunctions.append("</xqdoc:return>").append("\n");
        }
        visitChildren(context);

        for (String entry : invokedFunctions)
        {
            String namespace = null;
            String refLocalName = null;
            String[] tmp = entry.split(" ", 2);
            namespace = tmp[0];
            refLocalName = tmp[1];
            declaredFunctions.append("<xqdoc:invoked>").append("\n");
            declaredFunctions.append("<xqdoc:uri>");
            declaredFunctions.append(namespace);
            declaredFunctions.append("</xqdoc:uri>").append("\n");
            declaredFunctions.append("<xqdoc:name>");
            declaredFunctions.append(refLocalName);
            declaredFunctions.append("</xqdoc:name>").append("\n");
            declaredFunctions.append("</xqdoc:invoked>").append("\n");
        }

        for (String entry : referencedVariables)
        {
            String namespace = null;
            String refLocalName = null;
            String[] tmp = entry.split(" ", 2);
            namespace = tmp[0];
            refLocalName = tmp[1];
            declaredFunctions.append("<xqdoc:ref-variable>").append("\n");
            declaredFunctions.append("<xqdoc:uri>");
            declaredFunctions.append(namespace);
            declaredFunctions.append("</xqdoc:uri>").append("\n");
            declaredFunctions.append("<xqdoc:name>");
            declaredFunctions.append(refLocalName);
            declaredFunctions.append("</xqdoc:name>").append("\n");
            declaredFunctions.append("</xqdoc:ref-variable").append("\n");
        }

        declaredFunctions.append(printBody(context));

        declaredFunctions.append("</xqdoc:function>").append("\n");
        return null;
    }

    private StringBuffer printBody(ParserRuleContext context) {
        StringBuffer bodyBuffer = new StringBuffer();
        bodyBuffer.append("<xqdoc:body xml:space=\"preserve\"><![CDATA[");
        int a = context.start.getStartIndex();
        int b = context.stop.getStopIndex();
        Interval interval = new Interval(a,b);
        bodyBuffer.append(context.start.getInputStream().getText(interval));
        bodyBuffer.append("]]></xqdoc:body>").append("\n");
        return bodyBuffer;
    }

    @Override
    public String visitXqDocComment(XQueryParser.XqDocCommentContext context)
    {
        xqDocCommentContext = context;
        return null;
    }

    @Override
    public String visitFunctionCall(XQueryParser.FunctionCallContext context)
    {
        // Separate the function name into namspace prefix and localname
        String namespacePrefix = null;
        String namespace = null;
        String localName = null;
        String[] tmp = context.eqName().getText().split(":", 2);
        if (tmp.length > 1) {
            namespacePrefix = tmp[0];
            localName = tmp[1];
        } else {
            localName = tmp[0];
        }

        // Get the actual namespace
        if (namespacePrefix == null) {
            if (defaultModuleFunctionNamespace != null) {
                namespace = defaultModuleFunctionNamespace;
            } else if (defaultFunctionNamespace != null) {
                namespace = defaultFunctionNamespace;
            }
        } else {
            namespace = (String) (uriModuleMap.get(namespacePrefix));
            if (namespace == null) {
                namespace = (String) (predefinedFunctionNamespaces
                        .get(namespacePrefix));
            }
        }

        // References a namespace we don't know about
        if (namespace == null)
            return null;

        if (encodeURIs) {
            namespace = encodeURI(namespace);
        }

        // Check the invokedFunctions (to see if it is already there)
        if (!invokedFunctions.contains(namespace + " " + localName)) {
            invokedFunctions.add(namespace + " " + localName);
        }
        return null;
    }

    @Override
    public String visitVarRef(XQueryParser.VarRefContext context)
    {
        // Separate the variable name into namspace prefix and localname
        String namespacePrefix = null;
        String namespace = null;
        String localName = null;
        String[] tmp = context.getText().split(":", 2);
        if (tmp.length > 1) {
            namespacePrefix = tmp[0];
            localName = tmp[1];
        } else {
            return null;
        }

        namespace = (String) (uriModuleMap.get(namespacePrefix));
        if (namespace == null) {
            namespace = (String) (predefinedFunctionNamespaces
                    .get(namespacePrefix));
        }

        // References a namespace we don't know about
        if (namespace == null)
            return null;

        if (encodeURIs) {
            namespace = encodeURI(namespace);
        }

        if (!referencedVariables.contains(namespace + " " + localName)) {
            referencedVariables.add(namespace + " " + localName);
        }
        return null;
    }

    /**
     * Encode the URI. Calls will be made to this method depending on the value
     * (encodeURI) set in the init(). Currently, only the "/" is encoded.
     *
     * @param uri
     *            The string to encode.
     * @return The encoded string.
     */
    private String encodeURI(String uri) {
        return uri.replaceAll("/", "~2F");
    }

}
