package org.exquery.xqdoc;

import org.antlr.v4.runtime.misc.Interval;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class XQueryVisitor extends XQueryParserBaseVisitor<String> {
    private StringBuffer stream;
    private XQueryParser.XqDocCommentContext xqDocCommentContext = null;
    DateTimeFormatter isoFormat = ISODateTimeFormat.dateTime();

    public XQueryVisitor(StringBuffer stream)
    {
        this.stream = stream;
    }

    private void printXQDocumentation()
    {
        if (xqDocCommentContext != null)
        {
            int a = xqDocCommentContext.start.getStartIndex();
            int b = xqDocCommentContext.stop.getStopIndex();
            Interval interval = new Interval(a,b);
            String xqDocBody = xqDocCommentContext.start.getInputStream().getText(interval);
            XQDocComment xqDocComment = new XQDocComment();
            xqDocComment.clear();
            xqDocComment.setComment(xqDocBody);
            String comment = xqDocComment.getXML().toString();
            stream.append(comment).append("\n");
            xqDocCommentContext = null;
        }
    }

    @Override
    public String visitModule(XQueryParser.ModuleContext context)
    {
        stream.append("<xqdoc:xqdoc xmlns:xqdoc=\"http://www.xqdoc.org/1.0\">").append("\n");
        stream.append("<xqdoc:control>").append("\n");
        stream.append("<xqdoc:date>");
        stream.append(new DateTime().toString(isoFormat));
        stream.append("</xqdoc:date>").append("\n");
        stream.append("<xqdoc:version>1.1</xqdoc:version>").append("\n");
        stream.append("</xqdoc:control>").append("\n");
        if (context.libraryModule().moduleDecl() != null)
        {
            XQueryParser.ModuleDeclContext moduleDeclContext = context.libraryModule().moduleDecl();
            stream.append("<xqdoc:module type=\"library\">").append("\n");
            stream.append("<xqdoc:uri>");
            String uriText = moduleDeclContext.uri.getText();
            stream.append(uriText.substring(1).substring(0, uriText.length() - 2));
            stream.append("</xqdoc:uri>").append("\n");
            stream.append("<xqdoc:name>");
            String prefixText = moduleDeclContext.prefix.getText();
            stream.append(prefixText);
            stream.append("</xqdoc:name>").append("\n");
            printXQDocumentation();

            stream.append("</xqdoc:module>").append("\n");
        }
        if (context.mainModule() != null)
        {
            stream.append("<xqdoc:module type=\"main\">").append("\n");
            printXQDocumentation();
            stream.append("</xqdoc:module>").append("\n");
        }
        visitChildren(context);
        stream.append("</xqdoc:xqdoc>").append("\n");
        return null;
    }

    @Override
    public String visitProlog(XQueryParser.PrologContext context)
    {
        xqDocCommentContext = null;
        if (context.functionDecl() != null)
        {
            stream.append("<xqdoc:functions>").append("\n");
            visitChildren(context);
            stream.append("</xqdoc:functions>").append("\n");
        }
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

        stream.append("<xqdoc:function>").append("\n");
        printXQDocumentation();
        stream.append("<xqdoc:name>");
        stream.append(localName);
        stream.append("</xqdoc:name>").append("\n");
        if (context.annotations() != null) {
            stream.append("<xqdoc:annotations>").append("\n");
            for (XQueryParser.AnnotationContext annotation: context.annotations().annotation())
            {
                stream.append("<xqdoc:annotation name=\"");
                stream.append(annotation.qName().getText());
                stream.append("\">").append("\n");
                if (annotation.annotList() != null) {
                    for (XQueryParser.AnnotationParamContext annotationParam: annotation.annotList().annotationParam())
                    {
                        stream.append("<xqdoc:literal>");
                        stream.append(annotationParam.stringLiteral().getText());
                        stream.append("</xqdoc:literal>").append("\n");
                    }
                }
                stream.append("</xqdoc:annotation>").append("\n");
            }
            stream.append("</xqdoc:annotations>").append("\n");
        }
        stream.append("<xqdoc:signature>declare function ");
        stream.append(localName);
        stream.append("(");
        if (functionParamsContext != null)
        {
            int a = functionParamsContext.start.getStartIndex();
            int b = functionParamsContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            stream.append(context.start.getInputStream().getText(interval));
        }
        stream.append(")");
        if (functionReturnContext != null)
        {
            stream.append(" ");
            int a = functionReturnContext.start.getStartIndex();
            int b = functionReturnContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            stream.append(context.start.getInputStream().getText(interval));
        }
        stream.append("</xqdoc:signature>").append("\n");
        if (functionParamsContext != null)
        {
            stream.append("<xqdoc:parameters>").append("\n");
            for (XQueryParser.FunctionParamContext functionParam: functionParamsContext.functionParam())
            {
                stream.append("<xqdoc:parameter>").append("\n");
                stream.append("<xqdoc:name>");
                stream.append(functionParam.name.getText());
                stream.append("</xqdoc:name>").append("\n");
                stream.append("<xqdoc:type");
                if (functionParam.type.sequenceType().occurrence != null)
                {
                    stream.append(" occurrence=\"");
                    stream.append(functionParam.type.sequenceType().occurrence.getText());
                    stream.append("\"");
                }
                stream.append(">");
                stream.append(functionParam.type.sequenceType().itemType().getText());
                stream.append("</xqdoc:type>").append("\n");
                stream.append("</xqdoc:parameter>").append("\n");
            }
            stream.append("</xqdoc:parameters>").append("\n");
        }
        if (functionReturnContext != null)
        {
            stream.append("<xqdoc:return>").append("\n");
            stream.append("<xqdoc:type");
            if (functionReturnContext.sequenceType().occurrence != null)
            {
                stream.append(" occurrence=\"");
                stream.append(functionReturnContext.sequenceType().occurrence.getText());
                stream.append("\"");
            }
            stream.append(">");
            stream.append(functionReturnContext.sequenceType().itemType().getText());
            stream.append("</xqdoc:type>").append("\n");
            stream.append("</xqdoc:return>").append("\n");
        }
        visitChildren(context);
        stream.append("<xqdoc:body xml:space=\"preserve\">");
        int a = context.start.getStartIndex();
        int b = context.stop.getStopIndex();
        Interval interval = new Interval(a,b);
        stream.append(context.start.getInputStream().getText(interval));
        stream.append("</xqdoc:body>").append("\n");
        stream.append("</xqdoc:function>").append("\n");
        return null;
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
        stream.append("<xqdoc:invoked>").append("\n");
        stream.append("<xqdoc:name>");
        stream.append(context.functionName().getText());
        stream.append("</xqdoc:name>").append("\n");
        stream.append("</xqdoc:invoked>").append("\n");
        return null;
    }

    @Override
    public String visitVariableReference(XQueryParser.VariableReferenceContext context)
    {
        stream.append("<xqdoc:ref-variable>").append("\n");
        stream.append("<xqdoc:name>");
        stream.append(context.getText());
        stream.append("</xqdoc:name>").append("\n");
        stream.append("</xqdoc:ref-variable>").append("\n");
        return null;
    }
}
