/*
 * Copyright (c)2005 Elsevier, Inc.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */

package org.exquery.xqdoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * This class knows hows to 'parse' through a string of text that consists of a
 * xqDoc Comment block. Longer term, this logic should probably be embedded in
 * the actual xqDoc grammar, but for now it resides here. Perhaps when my
 * experience with ANTLR grows, I will move this logic. The class does assume
 * that a xqDoc comment is separated into 'lines'.
 * 
 * @author Darin McBeath
 * @version 1.0
 */
public class XQDocComment {

	// Buffer array for holding the current xqDoc comment block
	private StringBuffer[] xqDocCommentBlock = new StringBuffer[XQDOC_STATE_LAST];

	// Current xqDoc Comment State ... valid values are -1 to 9
	private int xqDocCommentState = -1;

	// String of current xqDoc comment
	private String xqDocComment;

	// xqDoc XML tag for comments
	private static final String XQDOC_COMMENT_TAG = "comment";

	// Various xqDoc Comment States and Names
	private static final int XQDOC_STATE_DESCRIPTION = 0;

	private static final int XQDOC_STATE_AUTHOR = 1;

	private static final String XQDOC_COMMENT_AUTHOR = "@author";

	private static final int XQDOC_STATE_VERSION = 2;

	private static final String XQDOC_COMMENT_VERSION = "@version";

	private static final int XQDOC_STATE_PARAM = 3;

	private static final String XQDOC_COMMENT_PARAM = "@param";

	private static final int XQDOC_STATE_RETURN = 4;

	private static final String XQDOC_COMMENT_RETURN = "@return";

	private static final int XQDOC_STATE_ERROR = 5;

	private static final String XQDOC_COMMENT_ERROR = "@error";

	private static final int XQDOC_STATE_DEPRECATED = 6;

	private static final String XQDOC_COMMENT_DEPRECATED = "@deprecated";

	private static final int XQDOC_STATE_SEE = 7;

	private static final String XQDOC_COMMENT_SEE = "@see";

	private static final int XQDOC_STATE_SINCE = 8;

	private static final String XQDOC_COMMENT_SINCE = "@since";

	private static final int XQDOC_STATE_LAST = 9;

	private static final String BEGIN_XQDOC_COMMENT = "(:~";

	private static final String END_XQDOC_COMMENT = ":)";

	// The order of the following tags must match the order of the values
	// assigned to the various xqDoc comment states.
	private static final String[] XQDOC_STATE_TAG = { "description", "author",
			"version", "param", "return", "error", "deprecated", "see", "since" };

	/**
	 * Initailize the XQDocComment object for processing of a xqDoc comment
	 * block. This includes clearing the various buffers and setting the current
	 * comment state to 'unknown'.
	 *  
	 */
	public void clear() {
		xqDocComment = null;
		xqDocCommentState = -1;
		for (int i = 0; i < xqDocCommentBlock.length; i++) {
			xqDocCommentBlock[i] = new StringBuffer(512);
		}
	}

	/**
	 * Set the internal buffer with the specified xqDoc comment block. This
	 * method is invoked (via XQDocContext) via the Parser for each xqDoc
	 * comment block encountered while parsing the module.
	 * 
	 * @param comment
	 *            A xqDoc comment block
	 */
	public void setComment(String comment) {
		xqDocComment = comment;
	}

	/**
	 * Loop through the array of string buffers for the xqDoc comment block and
	 * construct a complete comment block.
	 * 
	 * @return The serialized xqDoc XML for the current xqDoc comment block
	 */
	public StringBuffer getXML() {
		StringBuffer sb = new StringBuffer(1024);
		if (xqDocComment != null) {
			buildXQDocCommentSection();
			sb.append(XQDocXML.buildBeginTag(XQDOC_COMMENT_TAG));
			for (int i = 0; i < xqDocCommentBlock.length; i++) {
				sb.append(xqDocCommentBlock[i]);
			}
			sb.append(XQDocXML.buildEndTag(XQDOC_COMMENT_TAG));
		}
		return sb;
	}

	private String trimmedString(String text) {
	    if (xqDocCommentState == 0) {
	        return text;
        } else {
	        return text.trim();
        }
    }

	/**
	 * Append the current comment line to the comment buffer associated with the
	 * current xqDoc comment state.
	 * 
	 * @param line
	 *            Current line from module containing a xqDoc comment
	 * @param index
	 *            Current xqDoc comment state
	 */
	private void xqDocCommentStateConcat(String line, int index) {
		int last = line.indexOf(END_XQDOC_COMMENT);
		if (last == -1) {
			last = line.length();
		}
		if (index == -1) {
			int i;
			if ((i = line.indexOf(BEGIN_XQDOC_COMMENT)) > -1) {
				xqDocCommentBlock[xqDocCommentState].append(
				        trimmedString(line.substring(i
						+ BEGIN_XQDOC_COMMENT.length(), last)));
// 			} else if ((i = line.indexOf(":")) > -1) {
 			} else if (line.matches("^\\s*:.*")) {
 				i = line.indexOf(":");
				if (i < last) {
				    String trimmedLine = trimmedString(line.substring(i + 1, last));
				    if (trimmedLine.length() > 0) {
                        if (xqDocCommentState == 0 && xqDocCommentBlock[xqDocCommentState].length() > 19) {
                            xqDocCommentBlock[xqDocCommentState].append("\n");
                        }
                        xqDocCommentBlock[xqDocCommentState].append(trimmedLine);
                    }
				}
				// Get up to the closing comment
				else if (last != line.length()) {
					xqDocCommentBlock[xqDocCommentState].append(trimmedString(line.substring(
							0, last)));
				}
			} else {
				xqDocCommentBlock[xqDocCommentState].append(trimmedString(line.substring(0,
						last)));
			}
		} else {
			xqDocCommentBlock[xqDocCommentState].append(trimmedString(line.substring(index,
					last)));
		}
	}

	/**
	 * Begin a new comment within the current xqDoc comment state by appending
	 * this comment to the buffer associated with the current xqDoc comment
	 * state.
	 *  
	 */
	private void xqDocCommentStateBegin() {
		xqDocCommentBlock[xqDocCommentState].append(XQDocXML
				.buildBeginTag(XQDOC_STATE_TAG[xqDocCommentState]));
	}

	/**
	 * Close a comment within the current xqDoc comment state by appending this
	 * comment to the buffer associated with the current xqDoc comment state.
	 *  
	 */
	private void xqDocCommentStateClose() {
		xqDocCommentBlock[xqDocCommentState].append(XQDocXML
				.buildEndTag(XQDOC_STATE_TAG[xqDocCommentState]));
	}

	/**
	 * Process the xqDoc comment block. This includes converting the String of
	 * xqDoc comment (set by the parser) into a list of 'lines'. Each line will
	 * then be iteratively processed by invoking processXQDocLine.
	 * 
	 * @throws XQDocRuntimeException
	 *             Problems processing the xqDoc comment
	 */
	private void buildXQDocCommentSection() throws XQDocRuntimeException {
		try {
			if (xqDocComment == null)
				return;

			BufferedReader br = new BufferedReader(new StringReader(
					xqDocComment));
			String line = null;
			ArrayList lines = new ArrayList();
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();

			for (int i = 0; i < lines.size(); i++) {
				processXQDocLine((String) lines.get(i));
			}
		} catch (IOException ex) {
			throw new XQDocRuntimeException(
					"Problems processing the comment block.", ex);
		}
	}

	/**
	 * Process the line and determine the 'state' of the comment. In other
	 * words, is this for an 'author', 'version', 'description', etc. Then,
	 * append the comment information to the correct buffer (depending on the
	 * state).
	 * 
	 * @param line
	 *            Current line of xqDoc comment
	 */
	private void processXQDocLine(String line) {
		int index;
		if ((index = line.indexOf(XQDOC_COMMENT_PARAM)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_PARAM;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_PARAM.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_RETURN)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_RETURN;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_RETURN.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_ERROR)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_ERROR;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_ERROR.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_DEPRECATED)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_DEPRECATED;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index
					+ XQDOC_COMMENT_DEPRECATED.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_SEE)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_SEE;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_SEE.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_SINCE)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_SINCE;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_SINCE.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_AUTHOR)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_AUTHOR;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index + XQDOC_COMMENT_AUTHOR.length());
		} else if ((index = line.indexOf(XQDOC_COMMENT_VERSION)) > -1) {
			xqDocCommentStateClose();
			xqDocCommentState = XQDOC_STATE_VERSION;
			xqDocCommentStateBegin();
			xqDocCommentStateConcat(line, index
					+ XQDOC_COMMENT_VERSION.length());
		} else {
			if (xqDocCommentState == -1) {
				xqDocCommentState = XQDOC_STATE_DESCRIPTION;
				xqDocCommentStateBegin();
			}
			// Concatenate to previous state
			xqDocCommentStateConcat(line, -1);
		}

		if (line.indexOf(END_XQDOC_COMMENT) > -1) {
			xqDocCommentStateClose();
		}
	}
}