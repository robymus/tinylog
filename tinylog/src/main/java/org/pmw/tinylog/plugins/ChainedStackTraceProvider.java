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
 * Class to chain several StackTraceElementProviders after each other.
 * To chain more than two instances, several instances have to be created, thus leading to decreased performance.
 * This is probably acceptable, as using more than two plugins of the same type at the same time is not probable. 
 * 
 * @author r@r2.io
 */
public class ChainedStackTraceProvider implements StackTraceProvider {

	private final StackTraceProvider prev;
	private final StackTraceProvider cur;
	
	/**
	 * Construct a new chained instance, chaining two instances
	 * 
	 * @param prev 
	 *            the previously active instance (will be executed first)
	 * @param cur 
	 *            the currently added instance (will be executed second)
	 */
	public ChainedStackTraceProvider(StackTraceProvider prev, StackTraceProvider cur) {
		this.prev = prev;
		this.cur = cur;
	}

	@Override
	public StackTraceElement getStackTraceElement(int depth, boolean onlyClassName) {
		// note: depth is increased as we have an additional reference (this chain) object in the call stack
		StackTraceElement ret = prev.getStackTraceElement(depth+1, onlyClassName);
		if (ret != null) return ret;
		else return cur.getStackTraceElement(depth+1, onlyClassName);
	}

}
