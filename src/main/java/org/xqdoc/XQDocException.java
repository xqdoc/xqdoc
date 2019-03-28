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

/**
 * The base xqDoc exception that will be used by various
 * xqDoc drivers and the XQDocController.
 * 
 * @author Darin McBeath
 * @version 1.0
 */
public class XQDocException extends Exception {

	/**
	 *
	 */
	public XQDocException() {
	}

	/**
	 *
	 * @param message
	 */
	public XQDocException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message
	 * @param cause
	 */
	public XQDocException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 *
	 * @param cause
	 */
	public XQDocException(Throwable cause) {
		super(cause);
	}
}