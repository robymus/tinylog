/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.pmw.tinylog.plugins;

/**
 * Wrapper class to contain all plugins in a configuration.
 * This way it is a single extension point when new interfaces are added.
 * 
 * @author Robert Elek <r@r2.io>
 */
public class Plugins {

	private ExceptionSanitizer exceptionSanitizer;
	private StackTraceProvider stackTraceProvider;
	
	/**
	 * Creates an empty plugin list
	 */
	public Plugins() {
		clear();
	}
	
	/**
	 * Private constructor to create a copy
	 */
	private Plugins(Plugins source) {
		exceptionSanitizer = source.exceptionSanitizer;
		stackTraceProvider = source.stackTraceProvider;
	}
	
	/**
	 * Removes all plugins
	 */
	public void clear() {
		exceptionSanitizer = null;
		stackTraceProvider = null;
	}
	
	public boolean hasExceptionSanitizer() {
		return exceptionSanitizer != null;
	}
	
	public ExceptionSanitizer getExceptionSanitizer() {
		return exceptionSanitizer;
	}
	
	/**
	 * Adds a new ExceptionSanitizer, creating a chained instance if it exists already
	 */
	private synchronized void addExceptionSanitizer(ExceptionSanitizer e) {
		if (exceptionSanitizer == null) exceptionSanitizer = e;
		else exceptionSanitizer = new ChainedExceptionSanitizer(exceptionSanitizer, e);
	}
		
	public boolean hasStackTraceProvider() {
		return stackTraceProvider != null;
	}
	
	public StackTraceProvider getStackTraceProvider() {
		return stackTraceProvider;
	}

	/**
	 * Adds a new StackTraceProvider, creating a chained instance if it exists already
	 */
	private synchronized void addStackTraceProvider(StackTraceProvider s) {
		if (stackTraceProvider == null) stackTraceProvider = s;
		else stackTraceProvider = new ChainedStackTraceProvider(stackTraceProvider, s);
	}

	
	/**
	 * Add a new plugin, dynamically checking type against all supported interfaces
	 */
	public void addPlugin(Object o) {
		if (o instanceof ExceptionSanitizer) {
			addExceptionSanitizer((ExceptionSanitizer)o);
		}
		if (o instanceof StackTraceProvider) {
			addStackTraceProvider((StackTraceProvider)o);
		}
	}
	
	/**
	 * Copies this object
	 */
	public Plugins copy() {
		return new Plugins(this);
	}
}
