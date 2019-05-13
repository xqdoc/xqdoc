package org.xqdoc;

/**
 *
 */
public class ImportDeclaration {

    private String uri = null;
    private String importType = null;
    private String comment = null;
    private String location = null;

    /**
     *
     * @param uri
     * @param importType
     * @param location
     * @param comment
     */
    public ImportDeclaration(String uri, String importType, String location, String comment)
    {
        this.uri = uri;
        this.importType = importType;
        this.location = location;
        this.comment = comment;
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
        buffer.append("</xqdoc:import>\n");
        return buffer.toString();
    }
}
