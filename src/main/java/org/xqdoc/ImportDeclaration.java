package org.xqdoc;

/**
 *
 */
public class ImportDeclaration {

    private String prefix = null;
    private String uri = null;
    private String importType = null;
    private String comment = null;
    private String location = null;
    private String body = null;

    /**
     *
     * @param uri
     * @param importType
     * @param location
     * @param comment
     */
    public ImportDeclaration(String prefix, String uri, String importType, String location, String comment, String body)
    {
        this.prefix = prefix;
        this.uri = uri;
        this.importType = importType;
        this.location = location;
        this.comment = comment;
        this.body = body;
    }

    /**
     *
     * @return
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<xqdoc:import type=\"");
        buffer.append(this.importType);
        buffer.append("\"");
        if (this.prefix != null) {
            buffer.append(" prefix=\"");
            buffer.append(this.prefix);
            buffer.append("\"");
        }
        if (this.location != null) {
            buffer.append(" location=\"");
            buffer.append(this.location);
            buffer.append("\"");
        }
        buffer.append(">").append("\n");
        buffer.append("<xqdoc:uri>");
        buffer.append(this.uri);
        buffer.append("</xqdoc:uri>\n");
        if (this.comment != null)
        {
            buffer.append(this.comment);
        }
        if (this.body != null)
        {
            buffer.append(this.body);
        }
        buffer.append("</xqdoc:import>\n");
        return buffer.toString();
    }
}
