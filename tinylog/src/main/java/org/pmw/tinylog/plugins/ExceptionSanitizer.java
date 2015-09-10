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
 * Interface for customized plugins sanitizing exception stack traces
 * 
 * @author r@r2.io
 */
public interface ExceptionSanitizer {

	/**
	 * This method should return the stack trace element at depth specified in the parameter
	 * 
	 * @param exception 
	 *            the Throwable for which stack trace should be sanitized
	 * @return a Throwable with potentially modified StackTrace
	 */
	public Throwable sanitizeException(final Throwable exception);

}
