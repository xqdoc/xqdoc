package org.exquery.xqdoc;

import org.antlr.v4.runtime.misc.Interval;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.PrintStream;

public class XQueryVisitor extends XQueryParserBaseVisitor<String> {
    private PrintStream stream;
    private XQueryParser.XqDocCommentContext xqDocCommentContext = null;
    DateTimeFormatter isoFormat = ISODateTimeFormat.dateTime();

    public XQueryVisitor(PrintStream stream)
    {
        this.stream = stream;
    }

    private void printXQDocumentation()
    {
        if (xqDocCommentContext != null)
        {
            stream.println("<xqdoc:comment>");
            stream.println("<xqdoc:body>");
            int a = xqDocCommentContext.start.getStartIndex();
            int b = xqDocCommentContext.stop.getStopIndex();
            Interval interval = new Interval(a,b);
            stream.print(xqDocCommentContext.start.getInputStream().getText(interval));
            stream.println("</xqdoc:body>");
            stream.println("</xqdoc:comment>");
            xqDocCommentContext = null;
        }
    }

    @Override
    public String visitModule(XQueryParser.ModuleContext context)
    {
        stream.println("<xqdoc:xqdoc xmlns:xqdoc=\"http://www.xqdoc.org/1.0\">");
        stream.println("<xqdoc:control>");
        stream.print("<xqdoc:date>");
        stream.print(new DateTime().toString(isoFormat));
        stream.println("</xqdoc:date>");
        stream.println("<xqdoc:version>1.1</xqdoc:version>");
        stream.println("</xqdoc:control>");
        if (context.libraryModule().moduleDecl() != null)
        {
            XQueryParser.ModuleDeclContext moduleDeclContext = context.libraryModule().moduleDecl();
            stream.println("<xqdoc:module type=\"library\">");
            stream.print("<xqdoc:uri>");
            String uriText = moduleDeclContext.uri.getText();
            stream.print(uriText.substring(1).substring(0, uriText.length() - 2));
            stream.println("</xqdoc:uri>");
            stream.print("<xqdoc:name>");
            String prefixText = moduleDeclContext.prefix.getText();
            stream.print(prefixText);
            stream.println("</xqdoc:name>");
            printXQDocumentation();

            stream.println("</xqdoc:module>");
        }
        if (context.mainModule() != null)
        {
            stream.println("<xqdoc:module type=\"main\">");
            printXQDocumentation();
            stream.println("</xqdoc:module>");
        }
        visitChildren(context);
        stream.println("</xqdoc:xqdoc>");
        return null;
    }

    @Override
    public String visitProlog(XQueryParser.PrologContext context)
    {
        xqDocCommentContext = null;
        if (context.functionDecl() != null)
        {
            stream.println("<xqdoc:functions>");
            visitChildren(context);
            stream.println("</xqdoc:functions>");
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

        stream.println("<xqdoc:function>");
        printXQDocumentation();
        stream.print("<xqdoc:name>");
        stream.print(localName);
        stream.println("</xqdoc:name>");
        if (context.annotations() != null) {
            stream.println("<xqdoc:annotations>");
            for (XQueryParser.AnnotationContext annotation: context.annotations().annotation())
            {
                stream.print("<xqdoc:annotation name=\"");
                stream.print(annotation.qName().getText());
                stream.println("\">");
                if (annotation.annotList() != null) {
                    for (XQueryParser.AnnotationParamContext annotationParam: annotation.annotList().annotationParam())
                    {
                        stream.print("<xqdoc:literal>");
                        stream.print(annotationParam.stringLiteral().getText());
                        stream.println("</xqdoc:literal>");
                    }
                }
                stream.println("</xqdoc:annotation>");
            }
            stream.println("</xqdoc:annotations>");
        }
        stream.print("<xqdoc:signature>declare function ");
        stream.print(localName);
        stream.print("(");
        if (functionParamsContext != null)
        {
            int a = functionParamsContext.start.getStartIndex();
            int b = functionParamsContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            stream.print(context.start.getInputStream().getText(interval));
        }
        stream.print(")");
        if (functionReturnContext != null)
        {
            stream.print(" ");
            int a = functionReturnContext.start.getStartIndex();
            int b = functionReturnContext.stop.getStopIndex();
            Interval interval = new Interval(a, b);
            stream.print(context.start.getInputStream().getText(interval));
        }
        stream.println("</xqdoc:signature>");
        if (functionParamsContext != null)
        {
            stream.println("<xqdoc:parameters>");
            for (XQueryParser.FunctionParamContext functionParam: functionParamsContext.functionParam())
            {
                stream.println("<xqdoc:parameter>");
                stream.print("<xqdoc:name>");
                stream.print(functionParam.name.getText());
                stream.println("</xqdoc:name>");
                stream.print("<xqdoc:type");
                if (functionParam.type.sequenceType().occurrence != null)
                {
                    stream.print(" occurrence=\"");
                    stream.print(functionParam.type.sequenceType().occurrence.getText());
                    stream.print("\"");
                }
                stream.print(">");
                stream.print(functionParam.type.sequenceType().itemType().getText());
                stream.println("</xqdoc:type>");
                stream.println("</xqdoc:parameter>");
            }
            stream.println("</xqdoc:parameters>");
        }
        if (functionReturnContext != null)
        {
            stream.println("<xqdoc:return>");
            stream.print("<xqdoc:type");
            if (functionReturnContext.sequenceType().occurrence != null)
            {
                stream.print(" occurrence=\"");
                stream.print(functionReturnContext.sequenceType().occurrence.getText());
                stream.print("\"");
            }
            stream.print(">");
            stream.print(functionReturnContext.sequenceType().itemType().getText());
            stream.println("</xqdoc:type>");
            stream.println("</xqdoc:return>");
        }
        visitChildren(context);
        stream.print("<xqdoc:body xml:space=\"preserve\">");
        int a = context.start.getStartIndex();
        int b = context.stop.getStopIndex();
        Interval interval = new Interval(a,b);
        stream.print(context.start.getInputStream().getText(interval));
        stream.println("</xqdoc:body>");
        stream.println("</xqdoc:function>");
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
        stream.println("<xqdoc:invoked>");
        stream.print("<xqdoc:name>");
        stream.print(context.functionName().getText());
        stream.println("</xqdoc:name>");
        stream.println("</xqdoc:invoked>");
        return null;
    }

    @Override
    public String visitVariableReference(XQueryParser.VariableReferenceContext context)
    {
        stream.println("<xqdoc:ref-variable>");
        stream.print("<xqdoc:name>");
        stream.print(context.getText());
        stream.println("</xqdoc:name>");
        stream.println("</xqdoc:ref-variable>");
        return null;
    }
}
