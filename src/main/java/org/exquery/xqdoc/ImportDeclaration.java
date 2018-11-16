package org.exquery.xqdoc;

public class ImportDeclaration {

    private String uri = null;
    private String importType = null;
    private String comment = null;

    public ImportDeclaration(String uri, String importType, String comment)
    {
        this.uri = uri;
        this.importType = importType;
        this.comment = comment;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<xqdoc:import type=\"");
        buffer.append(this.importType);
        buffer.append("\">\n");
        buffer.append("<xqdoc:uri>");
        buffer.append(this.uri);
        buffer.append("/<xqdoc:uri>\n");
        if (this.comment != null)
        {
            buffer.append(this.comment);
        }
        buffer.append("/<xqdoc:import>\n");
        return buffer.toString();
    }
}
