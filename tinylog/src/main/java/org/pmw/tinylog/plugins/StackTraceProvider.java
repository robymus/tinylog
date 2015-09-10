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
 * Interface for customized plugins providing StackTraceElement retrieval
 * 
 * @author Robert Elek <r@r2.io>
 */
public interface StackTraceProvider {

	/**
	 * This method should return the stack trace element at depth specified in the parameter
	 * 
	 * @param depth 
	 *            the depth where the important information is (and should be retrieved)
	 * @param onlyClassName 
	 *            if true, only class name is used in the StackTraceElement, so optimized retrieval may be used 
	 * @return when returning null, the original logic will continue processing
	 */
	public StackTraceElement getStackTraceElement(final int depth, final boolean onlyClassName);

}
