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
 * Class to chain several ExceptionSanitizers after each other.
 * To chain more than two instances, several instances have to be created, thus leading to decreased performance.
 * This is probably acceptable, as using more than two plugins of the same type at the same time is not probable. 
 * 
 * @author r@r2.io
 */
public class ChainedExceptionSanitizer implements ExceptionSanitizer {

	private final ExceptionSanitizer prev;
	private final ExceptionSanitizer cur;
	
	/**
	 * Construct a new chained instance, chaining two instances
	 * 
	 * @param prev 
	 *            the previously active instance (will be executed first)
	 * @param cur 
	 *            the currently added instance (will be executed second)
	 */
	public ChainedExceptionSanitizer(ExceptionSanitizer prev, ExceptionSanitizer cur) {
		this.prev = prev;
		this.cur = cur;
	}
	
	@Override
	public Throwable sanitizeException(Throwable exception) {
		return cur.sanitizeException(prev.sanitizeException(exception));
	}

}
