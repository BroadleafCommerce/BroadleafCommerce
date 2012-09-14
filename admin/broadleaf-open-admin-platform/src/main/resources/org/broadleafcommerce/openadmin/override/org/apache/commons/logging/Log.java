/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;

/**
 * GWT compatible implementation of the commons.logging.Log interface
 * 
 * @author jfischer
 *
 */
public class Log implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private void log(Object arg0, Throwable arg1) {
		System.out.println(arg0 + " : " + arg1.getMessage());
		GWT.log((String) arg0, arg1);
	}
	
	private void log(Object arg0) {
		System.out.println(arg0);
		GWT.log((String) arg0, null);
	}
	
	public void debug(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void debug(Object arg0) {
		log(arg0);
	}

	public void error(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void error(Object arg0) {
		log(arg0);
	}

	public void fatal(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void fatal(Object arg0) {
		log(arg0);
	}

	public void info(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void info(Object arg0) {
		log(arg0);
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isErrorEnabled() {
		return true;
	}

	public boolean isFatalEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

	public boolean isWarnEnabled() {
		return true;
	}

	public void trace(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void trace(Object arg0) {
		log(arg0);
	}

	public void warn(Object arg0, Throwable arg1) {
		log(arg0, arg1);
	}

	public void warn(Object arg0) {
		log(arg0);
	}
	
	
}
