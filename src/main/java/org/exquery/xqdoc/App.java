package org.exquery.xqdoc;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
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
        XQueryVisitor visitor = new XQueryVisitor(System.out);
        visitor.visit(fileContext);
    }
}
