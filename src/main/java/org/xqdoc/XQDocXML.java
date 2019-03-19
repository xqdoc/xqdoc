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

package org.xqdoc;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class has the responsibility for generating xqDoc XML. It contains
 * member variables that hold the main sections of the xqDoc XML. Then, when the
 * xqDoc XML is requested, it combines all of the sections into the final xqDoc
 * XML output.
 * 
 * Since a namespace can be specified when this object is created, it should be
 * easy to support different versions of xqDoc XML without necessarily adjusting
 * the xqDoc conversion programs.
 * 
 * @author Darin McBeath
 * @version 1.0
 */
public class XQDocXML {

	private static final Logger LOGGER = Logger.getLogger(XQDocXML.class.getName());

	// xqDoc tags used during output of xqDoc XML
	private static final String XQDOC_PREFIX = "xqdoc:";

	private static final String XQDOC_TAG = "xqdoc";

	private static final String XQDOC_CONTROL_TAG = "control";

	private static final String XQDOC_DATE_TAG = "date";

	private static final String XQDOC_VERSION_TAG = "version";

	private static final String XQDOC_MODULE_TAG = "module";

	private static final String XQDOC_URI_TAG = "uri";

	private static final String XQDOC_VARIABLES_TAG = "variables";

	private static final String XQDOC_VARIABLE_TAG = "variable";

	private static final String XQDOC_IMPORTS_TAG = "imports";

	private static final String XQDOC_IMPORT_TAG = "import";

	private static final String XQDOC_NAMESPACES_TAG = "namespaces";

	private static final String XQDOC_NAMESPACE_TAG = "namespace";

	private static final String XQDOC_ANNOTATIONS_TAG = "annotations";

	private static final String XQDOC_ANNOTATION_TAG = "annotation";

	private static final String XQDOC_LITERAL_TAG = "literal";

	private static final String XQDOC_FUNCTIONS_TAG = "functions";

	private static final String XQDOC_FUNCTION_TAG = "function";

	private static final String XQDOC_NAME_TAG = "name";

	private static final String XQDOC_SIGNATURE_TAG = "signature";

	private static final String XQDOC_BODY_TAG = "body";

	private static final String XQDOC_INVOKED_TAG = "invoked";

	private static final String XQDOC_REFER_VAR_TAG = "ref-variable";

	private static final String XQDOC_RETURN_TAG = "return";

	private static final String XQDOC_TYPE_TAG = "type";

	private static final String XQDOC_OCCURRENCE_ATTRIBUTE = "occurrence";

	// xqDoc XML Namespace
	private String xqDocNamespace;

	// Buffer for holding the xml control section
	private StringBuilder xmlControl = new StringBuilder();

	// Buffer for holding the xml module section
	private StringBuilder xmlModule = new StringBuilder();

	// Buffer for holding the xml import section
	private StringBuilder xmlImport = new StringBuilder();

	// Buffer for holding the xml namespace section
	private StringBuilder xmlNamespace = new StringBuilder();

	// Buffer for holding the xml variable section
	private StringBuilder xmlVariable = new StringBuilder();

	// Buffer for holding the xml function section
	private StringBuilder xmlFunction = new StringBuilder();

	/**
	 * Constructor.
	 * 
	 * @param xqDocNamespace
	 *            Namespace of xqDoc XML to create
	 */
	public XQDocXML(String xqDocNamespace) {
		this.xqDocNamespace = xqDocNamespace;
	}

	/**
	 * Build the control section for xqDoc XML. This will consist of the date
	 * when the xqDoc conversion package generated the XML as well as the
	 * version of the xqDoc conversion package used to generate the XML. This
	 * method will be invoked once for each module processed.
	 * 
	 * @param version
	 *            The xqDoc conversion program version
	 */
	public void buildControlSection(String version) {
		xmlControl.append(buildBeginTag(XQDOC_CONTROL_TAG));
		xmlControl.append(buildBeginTag(XQDOC_DATE_TAG));
		xmlControl.append((new Date()).toString());
		xmlControl.append(buildEndTag(XQDOC_DATE_TAG));
		xmlControl.append(buildBeginTag(XQDOC_VERSION_TAG));
		xmlControl.append(version);
		xmlControl.append(buildEndTag(XQDOC_VERSION_TAG));
		xmlControl.append(buildEndTag(XQDOC_CONTROL_TAG));
	}

	/**
	 * Build the library module section for the xqDoc XML (for library modules).
	 * This will consist of the library module uri, friendly name, xqDoc comment
	 * block, and the source code for the entire library module. Either this
	 * method (or the one for main modules) will be invoked once for each module
	 * processed.
	 * 
	 * @param uri
	 *            The library module uri.
	 * @param commonName
	 *            The 'user-friendly' name for the library module
	 * @param comment
	 *            The XQDocComment block
	 * @param moduleBody
	 *            The source code for the library module
	 */
	public void buildLibraryModuleSection(String uri, String commonName,
			XQDocComment comment, String moduleBody) {
		xmlModule.append("<" + XQDOC_PREFIX + XQDOC_MODULE_TAG
				+ " type='library'>");
		xmlModule.append(buildBeginTag(XQDOC_URI_TAG));
		xmlModule.append(uri);
		xmlModule.append(buildEndTag(XQDOC_URI_TAG));
		if (commonName != null) {
			xmlModule.append(buildBeginTag(XQDOC_NAME_TAG));
			xmlModule.append(commonName);
			xmlModule.append(buildEndTag(XQDOC_NAME_TAG));
		}
		xmlModule.append(comment.getXML());
		if (moduleBody != null) {
			xmlModule.append("<" + XQDOC_PREFIX + XQDOC_BODY_TAG
					+ " xml:space='preserve'>");
			xmlModule.append(XQDocXML.encodeXML(moduleBody));
			xmlModule.append(buildEndTag(XQDOC_BODY_TAG));
		}
		xmlModule.append(buildEndTag(XQDOC_MODULE_TAG));
	}

	/**
	 * Build the main module section of the returned xqDoc XML (for main
	 * modules). This will consist of the main module uri, friendly name, xqDoc
	 * comment block, and the source code for the entire main module. Either
	 * this method (or the one for library modules) will be invoked once for
	 * each module processed.
	 * 
	 * @param uri
	 *            The main module uri.
	 * @param commonName
	 *            The 'user-friendly' name for the main module
	 * @param comment
	 *            The XQDocComment block
	 * @param moduleBody
	 *            The source code for the main module
	 */
	public void buildMainModuleSection(String uri, String commonName,
			XQDocComment comment, String moduleBody) {
		xmlModule.append("<" + XQDOC_PREFIX + XQDOC_MODULE_TAG
				+ " type='main'>");
		xmlModule.append(buildBeginTag(XQDOC_URI_TAG));
		xmlModule.append(uri);
		xmlModule.append(buildEndTag(XQDOC_URI_TAG));
		if (commonName != null) {
			xmlModule.append(buildBeginTag(XQDOC_NAME_TAG));
			xmlModule.append(commonName);
			xmlModule.append(buildEndTag(XQDOC_NAME_TAG));
		}
		xmlModule.append(comment.getXML());
		if (moduleBody != null) {
			xmlModule.append("<" + XQDOC_PREFIX + XQDOC_BODY_TAG
					+ " xml:space='preserve'>");
			xmlModule.append(XQDocXML.encodeXML(moduleBody));
			xmlModule.append(buildEndTag(XQDOC_BODY_TAG));
		}
		xmlModule.append(buildEndTag(XQDOC_MODULE_TAG));
	}

	/**
	 * Append information to the import section of the returned xqDoc XML. This
	 * information will include the uri for the import as well as the xqDoc
	 * comment block associated with the import. This method will be called once
	 * for each module imported by either a library or main module.
	 *
	 * @param uri
	 *            The uri for the module imported.
	 * @param comment
	 *            The XQDocComment block
	 */
	public void buildImportSection(String uri, XQDocComment comment) {
		xmlImport.append(buildBeginTag(XQDOC_IMPORT_TAG));
		xmlImport.append(buildBeginTag(XQDOC_URI_TAG));
		xmlImport.append(uri);
		xmlImport.append(buildEndTag(XQDOC_URI_TAG));
		xmlImport.append(comment.getXML());
		xmlImport.append(buildEndTag(XQDOC_IMPORT_TAG));
	}

	/**
	 * Append information to the import section of the returned xqDoc XML. This
	 * information will include the uri for the import as well as the xqDoc
	 * comment block associated with the import. This method will be called once
	 * for each module imported by either a library or main module.
	 *
	 * @param prefix
	 *            The uri for the module imported.
	 * @param uri
	 *            The uri for the module imported.
	 */
	public void buildNamespaceSection(String prefix, String uri) {
		xmlNamespace.append("<");
		xmlNamespace.append(XQDOC_PREFIX);
		xmlNamespace.append(XQDOC_NAMESPACE_TAG);
		xmlNamespace.append(" prefix=\"");
		xmlNamespace.append(prefix);
		xmlNamespace.append("\" uri=\"");
		xmlNamespace.append(uri);
		xmlNamespace.append("\">");
		xmlNamespace.append(buildEndTag(XQDOC_NAMESPACE_TAG));
	}

	/**
	 * Append information to the variable section of the returned xqDoc XML.
	 * This information will include the defined global variable uri as well as
	 * the xqDoc comment block associated with the global variable. This method
	 * will be called once for each global variable declared by either a library
	 * or main module.
	 * 
	 * @param uri
	 *            The uri for the global variable.
	 * @param comment
	 *            The XQDocComment block
	 */
	public void buildVariableSection(String uri, XQDocComment comment) {
		xmlVariable.append(buildBeginTag(XQDOC_VARIABLE_TAG));
		xmlVariable.append(buildBeginTag(XQDOC_URI_TAG));
		xmlVariable.append(uri);
		xmlVariable.append(buildEndTag(XQDOC_URI_TAG));
		xmlVariable.append(comment.getXML());
		xmlVariable.append(buildEndTag(XQDOC_VARIABLE_TAG));
	}

	/**
	 * Construct the snippet of serialized xqDoc XML for the function. This
	 * information will consist of the function name, function signature, xqDoc
	 * comment block, source code for the function, global variables used by
	 * this function, and functions invoked from within this function. This
	 * method will be called once for each function declared in a library or
	 * main module.
	 * @param functionName
	 *       The local name for the current function
	 * @param functionSignature
	 *       The signature for the function
	 * @param comment
     *       The XQDocComment associated with the function
	 * @param functionBody
     *       The source code for the function
     * @param functionReturnType
	 * 		 The return type of the function
	 * @param functionReturnOccurrence
	 *       The number of occurrences of the returned item(s)
	 * @param invokedFunctions
     *       The list of functions invoked by this function
	 * @param referencedVariables
     *       The linked list of variables referenced by this function
	 * @param annotationList
	 *       The linked list of annotations
	 */
	public void buildFunctionSection(String functionName,
									 String functionSignature, XQDocComment comment,
									 String functionBody, String functionReturnType,
									 String functionReturnOccurrence, Set invokedFunctions,
									 Set referencedVariables, List annotationList) {

		xmlFunction.append(buildBeginTag(XQDOC_FUNCTION_TAG));
		xmlFunction.append(comment.getXML());
		xmlFunction.append(buildBeginTag(XQDOC_NAME_TAG));
		xmlFunction.append(functionName);
		xmlFunction.append(buildEndTag(XQDOC_NAME_TAG));
		if (!annotationList.isEmpty()) {
			xmlFunction.append(buildBeginTag(XQDOC_ANNOTATIONS_TAG));
			for (Object o:annotationList) {
				LinkedList l = (LinkedList)o;

				boolean isFirst = true;
				try {
                    for (Object item : l) {
                        if (isFirst) {
                            isFirst = false;
                            xmlFunction.append("<" + XQDOC_PREFIX + XQDOC_ANNOTATION_TAG
                                    + " name='" + item + "'>");
                        } else {
                            xmlFunction.append(buildBeginTag(XQDOC_LITERAL_TAG));
							xmlFunction.append(item);
                            xmlFunction.append(buildEndTag(XQDOC_LITERAL_TAG));
                        }
                    }
                    xmlFunction.append(buildEndTag(XQDOC_ANNOTATION_TAG));
                } catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Failure in building an annotation tag", e);
                }
			}
			xmlFunction.append(buildEndTag(XQDOC_ANNOTATIONS_TAG));
		}
		if (functionSignature != null) {
			xmlFunction.append(buildBeginTag(XQDOC_SIGNATURE_TAG));
			xmlFunction.append(functionSignature);
			xmlFunction.append(buildEndTag(XQDOC_SIGNATURE_TAG));
		}
		if (functionReturnType != null) {
			xmlFunction.append(buildBeginTag(XQDOC_RETURN_TAG));
			xmlFunction.append("<");
			xmlFunction.append(XQDOC_PREFIX);
			xmlFunction.append(XQDOC_TYPE_TAG);
			if (functionReturnOccurrence != null) {
				xmlFunction.append(" ");
				xmlFunction.append(XQDOC_OCCURRENCE_ATTRIBUTE);
				xmlFunction.append("='");
				xmlFunction.append(functionReturnOccurrence);
				xmlFunction.append("'");
			}
			xmlFunction.append(">");
			xmlFunction.append(functionReturnType);
			xmlFunction.append(buildEndTag(XQDOC_TYPE_TAG));
			xmlFunction.append(buildEndTag(XQDOC_RETURN_TAG));
		}
		xmlFunction.append(buildInvokedFunctions(invokedFunctions));
		xmlFunction.append(buildReferencedVariables(referencedVariables));
		if (functionBody != null) {
			xmlFunction.append("<" + XQDOC_PREFIX + XQDOC_BODY_TAG
					+ " xml:space='preserve'>");
			xmlFunction.append(XQDocXML.encodeXML(functionBody));
			xmlFunction.append(buildEndTag(XQDOC_BODY_TAG));
		}
		xmlFunction.append(buildEndTag(XQDOC_FUNCTION_TAG));
	}

	/**
	 * Construct the snippet of serialized xqDoc XML for the list of global
	 * variables referenced by the current function. The XML will contain the
	 * namespace uri and the local name for each global variable referenced.
	 * 
	 * @param referencedVariables
	 * @return The snippet of serialized xqDoc XML pertaining to the referenced
	 *         variables section (for the given function).
	 */
	private String buildReferencedVariables(Set referencedVariables) {
		StringBuilder rsp = new StringBuilder();
		if (!referencedVariables.isEmpty()) {
			Iterator it = referencedVariables.iterator();
			while (it.hasNext()) {
				String entry = (String) it.next();
				String namespace = null;
				String localName = null;
				String[] tmp = entry.split(" ", 2);
				namespace = tmp[0];
				localName = tmp[1];
				rsp.append(buildBeginTag(XQDOC_REFER_VAR_TAG));
				rsp.append(buildBeginTag(XQDOC_URI_TAG));
				rsp.append(namespace);
				rsp.append(buildEndTag(XQDOC_URI_TAG));
				rsp.append(buildBeginTag(XQDOC_NAME_TAG));
				rsp.append(localName);
				rsp.append(buildEndTag(XQDOC_NAME_TAG));
				rsp.append(buildEndTag(XQDOC_REFER_VAR_TAG));
			}
		}
		return rsp.toString();
	}

	/**
	 * Construct the snippet of serialized xqDoc XML for the list of functions
	 * invoked by the current function. The XML will contain the namespace uri
	 * and the local name for each invoked function.
	 * 
	 * @param invokedFunctions
	 *            The list of functions used by the current function
	 * @return The snippet of serialized xqDoc XML pertaining to the invoked
	 *         functions section (for the given function).
	 */
	private String buildInvokedFunctions(Set invokedFunctions) {
		StringBuilder rsp = new StringBuilder();
		if (!invokedFunctions.isEmpty()) {
			Iterator it = invokedFunctions.iterator();
			while (it.hasNext()) {
				String entry = (String) it.next();
				String namespace = null;
				String localName = null;
				String[] tmp = entry.split(" ", 2);
				namespace = tmp[0];
				localName = tmp[1];
				rsp.append(buildBeginTag(XQDOC_INVOKED_TAG));
				rsp.append(buildBeginTag(XQDOC_URI_TAG));
				rsp.append(namespace);
				rsp.append(buildEndTag(XQDOC_URI_TAG));
				rsp.append(buildBeginTag(XQDOC_NAME_TAG));
				rsp.append(localName);
				rsp.append(buildEndTag(XQDOC_NAME_TAG));
				rsp.append(buildEndTag(XQDOC_INVOKED_TAG));
			}
		}
		return rsp.toString();
	}

	/**
	 * Construct the xqDoc XML and return the XML as a serialized string. The
	 * XML will consist of the following sections:
	 * <ul>
	 * <li>control</li>
	 * <li>module declaration</li>
	 * <li>imported modules</li>
	 * <li>declared variables</li>
	 * <li>declared functions</li>
	 * </ul>
	 * 
	 * @return Serialized string of xqDoc XML
	 */
	public String getXML() {
		StringBuilder rsp = new StringBuilder();
		rsp.append(buildBeginTagWithNamespace(XQDOC_TAG, xqDocNamespace));
		rsp.append(xmlControl);
		rsp.append(xmlModule);
		if (xmlImport.length() > 0) {
			rsp.append(buildBeginTag(XQDOC_IMPORTS_TAG));
			rsp.append(xmlImport);
			rsp.append(buildEndTag(XQDOC_IMPORTS_TAG));
		}
		if (xmlNamespace.length() > 0) {
			rsp.append(buildBeginTag(XQDOC_NAMESPACES_TAG));
			rsp.append(xmlNamespace);
			rsp.append(buildEndTag(XQDOC_NAMESPACES_TAG));
		}
		if (xmlVariable.length() > 0) {
			rsp.append(buildBeginTag(XQDOC_VARIABLES_TAG));
			rsp.append(xmlVariable);
			rsp.append(buildEndTag(XQDOC_VARIABLES_TAG));
		}
		if (xmlFunction.length() > 0) {
			rsp.append(buildBeginTag(XQDOC_FUNCTIONS_TAG));
			rsp.append(xmlFunction);
			rsp.append(buildEndTag(XQDOC_FUNCTIONS_TAG));
		}
		rsp.append(buildEndTag(XQDOC_TAG));
		return rsp.toString();
	}

	/**
	 * Helper method to build a begin XML tag name for the specified name and
	 * namespace.
	 *
	 * @param name
	 *            XML Element name
	 * @param namespace
	 *            The namespace for this element and it's descendants
	 *
	 * @return Begin XML tag for the specified element name
	 */
	public static String buildBeginTagWithNamespace(String name,
													String namespace) {
		return "<" + XQDOC_PREFIX + name + " xmlns:xqdoc='" + namespace + "'>";
	}

	/**
	 * Helper method to build a begin XML tag name for the specified name.
	 *
	 * @param name
	 *            XML Element name
	 * @return Begin XML tag for the specified element name
	 */
	public static String buildBeginTag(String name) {
		return "<" + XQDOC_PREFIX + name + ">";
	}

	/**
	 * Helper method to build a begin XML tag name for the specified name.
	 *
	 * @param name
	 *            XML Element name
	 * @param tag
	 *            Value for the tag attribute of the XML element
	 * @return Begin XML tag for the specified element name
	 */
	public static String buildBeginTagWithTagAttribute(String name, String tag) {
		return "<" + XQDOC_PREFIX + name + " tag='" + tag + "'>";
	}

	/**
	 * Helper method to build an end XML tag name for the specified name.
	 * 
	 * @param name
	 *            XML Element name
	 * @return End XML tag for the specified element name
	 */
	public static String buildEndTag(String name) {
		return "</" + XQDOC_PREFIX + name + ">";
	}
	
	/**
	 * Encode the string.  In particular, the following
	 * characters will be replaced with the corresponding
	 * entity.
	 * <ul>
	 * <li>&amp; with &amp;amp;</li>
	 * <li>&lt; with &amp;lt;</li>
	 * <li>&gt; with &amp;gt;</li>
	 * </ul> 
	 * @param input The string to encode
	 * @return The encoded string
	 */
	public static String encodeXML(String input) {
		String tmp = input.replaceAll("&", "&amp;");
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");
		return tmp;
	}
}