/*
 * Copyright 2012 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.pmw.tinylog;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.sameContent;
import static org.pmw.tinylog.hamcrest.StringMatchers.containsPattern;
import static org.pmw.tinylog.hamcrest.StringMatchers.matchesPattern;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;

import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;
import org.pmw.tinylog.util.NullWriter;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.LogEntryValue;

/**
 * Tests for the logger.
 *
 * @see Logger
 */
public class LoggerTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 *
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(Logger.class);
	}

	/**
	 * Test trace enabled tester.
	 */
	@Test
	public final void testIsTraceEnabled() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.OFF).level(LoggerTest.class, Level.TRACE).activate();
		assertTrue(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.TRACE).level(LoggerTest.class, Level.OFF).activate();
		assertFalse(Logger.isTraceEnabled());

		Configurator.defaultConfig().level(Level.TRACE).writer(null).activate();
		assertFalse(Logger.isTraceEnabled());
	}

	/**
	 * Test trace methods.
	 */
	@Test
	public final void testTrace() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValues.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValues);
			Configurator.defaultConfig().writer(writer).level(Level.TRACE).activate();

			Logger.trace(new StringBuilder("Hello!"));
			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(Level.TRACE, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.trace("Hello!");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.TRACE, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.trace("Hello {}!", "World");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.TRACE, logEntry.getLevel());
			assertEquals("Hello World!", logEntry.getMessage());

			Exception exception = new Exception();

			Logger.trace(exception);
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.TRACE, logEntry.getLevel());
			assertEquals(exception, logEntry.getException());

			Logger.trace(exception, "Test");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.TRACE, logEntry.getLevel());
			assertEquals("Test", logEntry.getMessage());
			assertEquals(exception, logEntry.getException());

			Configurator.currentConfig().level(Level.DEBUG).activate();
			Logger.trace("Hello!");
			assertNull(writer.consumeLogEntry());
		}
	}

	/**
	 * Test debug enabled tester.
	 */
	@Test
	public final void testIsDebugEnabled() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertTrue(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertFalse(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.OFF).level(LoggerTest.class, Level.DEBUG).activate();
		assertTrue(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).level(LoggerTest.class, Level.OFF).activate();
		assertFalse(Logger.isDebugEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).writer(null).activate();
		assertFalse(Logger.isDebugEnabled());
	}

	/**
	 * Test debug methods.
	 */
	@Test
	public final void testDebug() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValues.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValues);
			Configurator.defaultConfig().writer(writer).level(Level.DEBUG).activate();

			Logger.debug(new StringBuilder("Hello!"));
			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(Level.DEBUG, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.debug("Hello!");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.DEBUG, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.debug("Hello {}!", "World");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.DEBUG, logEntry.getLevel());
			assertEquals("Hello World!", logEntry.getMessage());

			Exception exception = new Exception();

			Logger.debug(exception);
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.DEBUG, logEntry.getLevel());
			assertEquals(exception, logEntry.getException());

			Logger.debug(exception, "Test");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.DEBUG, logEntry.getLevel());
			assertEquals("Test", logEntry.getMessage());
			assertEquals(exception, logEntry.getException());

			Configurator.currentConfig().level(Level.INFO).activate();
			Logger.debug("Hello!");
			assertNull(writer.consumeLogEntry());
		}
	}

	/**
	 * Test info enabled tester.
	 */
	@Test
	public final void testIsInfoEnabled() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertTrue(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertTrue(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertFalse(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.OFF).level(LoggerTest.class, Level.INFO).activate();
		assertTrue(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.INFO).level(LoggerTest.class, Level.OFF).activate();
		assertFalse(Logger.isInfoEnabled());

		Configurator.defaultConfig().level(Level.INFO).writer(null).activate();
		assertFalse(Logger.isInfoEnabled());
	}

	/**
	 * Test info methods.
	 */
	@Test
	public final void testInfo() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValues.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValues);
			Configurator.defaultConfig().writer(writer).level(Level.INFO).activate();

			Logger.info(new StringBuilder("Hello!"));
			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(Level.INFO, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.info("Hello!");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.INFO, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.info("Hello {}!", "World");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.INFO, logEntry.getLevel());
			assertEquals("Hello World!", logEntry.getMessage());

			Exception exception = new Exception();

			Logger.info(exception);
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.INFO, logEntry.getLevel());
			assertEquals(exception, logEntry.getException());

			Logger.info(exception, "Test");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.INFO, logEntry.getLevel());
			assertEquals("Test", logEntry.getMessage());
			assertEquals(exception, logEntry.getException());

			Configurator.currentConfig().level(Level.WARNING).activate();
			Logger.info("Hello!");
			assertNull(writer.consumeLogEntry());
		}
	}

	/**
	 * Test warning enabled tester.
	 */
	@Test
	public final void testIsWarningEnabled() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertTrue(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertTrue(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertTrue(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.OFF).level(LoggerTest.class, Level.WARNING).activate();
		assertTrue(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.WARNING).level(LoggerTest.class, Level.OFF).activate();
		assertFalse(Logger.isWarningEnabled());

		Configurator.defaultConfig().level(Level.WARNING).writer(null).activate();
		assertFalse(Logger.isWarningEnabled());
	}

	/**
	 * Test warning methods.
	 */
	@Test
	public final void testWarning() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValues.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValues);
			Configurator.defaultConfig().writer(writer).level(Level.WARNING).activate();

			Logger.warn(new StringBuilder("Hello!"));
			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(Level.WARNING, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.warn("Hello!");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.WARNING, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.warn("Hello {}!", "World");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.WARNING, logEntry.getLevel());
			assertEquals("Hello World!", logEntry.getMessage());

			Exception exception = new Exception();

			Logger.warn(exception);
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.WARNING, logEntry.getLevel());
			assertEquals(exception, logEntry.getException());

			Logger.warn(exception, "Test");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.WARNING, logEntry.getLevel());
			assertEquals("Test", logEntry.getMessage());
			assertEquals(exception, logEntry.getException());

			Configurator.currentConfig().level(Level.ERROR).activate();
			Logger.warn("Hello!");
			assertNull(writer.consumeLogEntry());
		}
	}

	/**
	 * Test error enabled tester.
	 */
	@Test
	public final void testIsErrorEnabled() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.OFF).level(LoggerTest.class, Level.ERROR).activate();
		assertTrue(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.ERROR).level(LoggerTest.class, Level.OFF).activate();
		assertFalse(Logger.isErrorEnabled());

		Configurator.defaultConfig().level(Level.ERROR).writer(null).activate();
		assertFalse(Logger.isErrorEnabled());
	}

	/**
	 * Test error methods.
	 */
	@Test
	public final void testError() {
		for (boolean mode : new boolean[] { false, true }) {
			Set<LogEntryValue> requiredLogEntryValues = EnumSet.of(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
			if (mode) {
				/* Get stack trace from sun reflection */
				requiredLogEntryValues.add(LogEntryValue.CLASS);
			} else {
				/* Get stack trace from a throwable if necessary */
			}

			StoreWriter writer = new StoreWriter(requiredLogEntryValues);
			Configurator.defaultConfig().writer(writer).level(Level.ERROR).activate();

			Logger.error(new StringBuilder("Hello!"));
			LogEntry logEntry = writer.consumeLogEntry();
			assertEquals(Level.ERROR, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.error("Hello!");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.ERROR, logEntry.getLevel());
			assertEquals("Hello!", logEntry.getMessage());

			Logger.error("Hello {}!", "World");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.ERROR, logEntry.getLevel());
			assertEquals("Hello World!", logEntry.getMessage());

			Exception exception = new Exception();

			Logger.error(exception);
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.ERROR, logEntry.getLevel());
			assertEquals(exception, logEntry.getException());

			Logger.error(exception, "Test");
			logEntry = writer.consumeLogEntry();
			assertEquals(Level.ERROR, logEntry.getLevel());
			assertEquals("Test", logEntry.getMessage());
			assertEquals(exception, logEntry.getException());

			Configurator.currentConfig().level(Level.OFF).activate();
			Logger.error("Hello!");
			assertNull(writer.consumeLogEntry());
		}
	}

	/**
	 * Test stack trace deep based logging level enabled tester.
	 */
	@Test
	public final void testIsEnabledByStackTraceDeep() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.WARNING).level(LoggerTest.class, Level.DEBUG).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).level(LoggerTest.class, Level.WARNING).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertTrue(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).writer(null).activate();
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.TRACE));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.INFO));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.WARNING));
		assertFalse(Logger.isEnabled(Logger.DEEP_OF_STACK_TRACE, Level.ERROR));
	}

	/**
	 * Test stack trace element based logging level enabled tester.
	 */
	@Test
	public final void testIsEnabledByStackTraceElement() {
		StackTraceElement stackTraceElementA = new StackTraceElement("a.A", "?", "?", -1);
		StackTraceElement stackTraceElementB = new StackTraceElement("a.B", "?", "?", -1);

		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.ERROR));

		Configurator.defaultConfig().level(Level.WARNING).level("a.A", Level.DEBUG).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));
		assertFalse(Logger.isEnabled(stackTraceElementB, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementB, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementB, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).level("a.A", Level.WARNING).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementA, Level.ERROR));
		assertFalse(Logger.isEnabled(stackTraceElementB, Level.TRACE));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.DEBUG));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.INFO));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.WARNING));
		assertTrue(Logger.isEnabled(stackTraceElementB, Level.ERROR));

		Configurator.defaultConfig().level(Level.DEBUG).writer(null).activate();
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.TRACE));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.DEBUG));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.INFO));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.WARNING));
		assertFalse(Logger.isEnabled(stackTraceElementA, Level.ERROR));
	}

	/**
	 * Test stack trace deep based logging level getter.
	 */
	@Test
	public final void testGetLevelByStackTraceDeep() {
		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertEquals(Level.TRACE, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertEquals(Level.INFO, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertEquals(Level.WARNING, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertEquals(Level.ERROR, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertEquals(Level.OFF, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.WARNING).level(LoggerTest.class, Level.DEBUG).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.DEBUG).level(LoggerTest.class, Level.WARNING).activate();
		assertEquals(Level.WARNING, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));

		Configurator.defaultConfig().level(Level.DEBUG).writer(null).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(Logger.DEEP_OF_STACK_TRACE));
	}

	/**
	 * Test stack trace element based logging level getter.
	 */
	@Test
	public final void testGetLevelByStackTraceElement() {
		StackTraceElement stackTraceElementA = new StackTraceElement("a.A", "?", "?", -1);
		StackTraceElement stackTraceElementB = new StackTraceElement("a.B", "?", "?", -1);

		Configurator.defaultConfig().level(Level.TRACE).activate();
		assertEquals(Level.TRACE, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.DEBUG).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.INFO).activate();
		assertEquals(Level.INFO, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.WARNING).activate();
		assertEquals(Level.WARNING, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.ERROR).activate();
		assertEquals(Level.ERROR, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.OFF).activate();
		assertEquals(Level.OFF, Logger.getLevel(stackTraceElementA));

		Configurator.defaultConfig().level(Level.WARNING).level("a.A", Level.DEBUG).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(stackTraceElementA));
		assertEquals(Level.WARNING, Logger.getLevel(stackTraceElementB));

		Configurator.defaultConfig().level(Level.DEBUG).level("a.A", Level.WARNING).activate();
		assertEquals(Level.WARNING, Logger.getLevel(stackTraceElementA));
		assertEquals(Level.DEBUG, Logger.getLevel(stackTraceElementB));

		Configurator.defaultConfig().level(Level.DEBUG).writer(null).activate();
		assertEquals(Level.DEBUG, Logger.getLevel(stackTraceElementA));
	}

	/**
	 * Test output method with stack trace deep.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceDeep() throws InterruptedException {
		/* Test logging without writer */

		Configurator.defaultConfig().writer(null).activate();
		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.INFO, null, null, null);

		/* Initialize writer */

		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).level(Level.DEBUG).activate();

		/* Test logging of class */

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.INFO, null, "Hello!", new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.INFO, null, "Hello {}!", new Object[] { "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		Exception exception = new Exception();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.WARNING, exception, null, new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(exception, logEntry.getException());

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.ERROR, exception, "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("org.pmw.tinylog", Level.DEBUG).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.DEBUG, null, "Hello!", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("org.pmw.tinylog", null).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.INFO, null, "Hello {}!", new Object[] { new EvilObject() });
		assertNull(writer.consumeLogEntry());
		assertEquals("LOGGER ERROR: Failed to create log entry (" + RuntimeException.class.getName() + ")", getErrorStream().nextLine());

		/* Test failure of writing log entry */

		Configurator.currentConfig().writer(new EvilWriter()).activate();

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.ERROR, null, "Hello!", new Object[0]);
		assertEquals("LOGGER ERROR: Failed to write log entry (" + UnsupportedOperationException.class.getName() + ")", getErrorStream().nextLine());

		/* Test using writing thread */

		Configurator.currentConfig().writer(writer).writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(Logger.DEEP_OF_STACK_TRACE, Level.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

	/**
	 * Test output method with stack trace element.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithStackTraceElement() throws InterruptedException {
		StackTraceElement stackTraceElement = new StackTraceElement("com.test.MyClass", "?", "?", -1);

		/* Test logging without writer */

		Configurator.defaultConfig().writer(null).activate();
		Logger.output(stackTraceElement, Level.INFO, null, null, null);

		/* Initialize writer */

		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(Level.DEBUG).activate();

		Logger.output(stackTraceElement, Level.INFO, null, "Hello!", new Object[0]);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("com.test.MyClass", logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.MESSAGE);
		Configurator.currentConfig().writer(writer).level(Level.INFO).activate();

		Logger.output(stackTraceElement, Level.DEBUG, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());

		Logger.output(stackTraceElement, Level.INFO, null, "Hello {}!", new Object[] { "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.currentConfig().writer(writer).level(Level.INFO).activate();

		Exception exception = new Exception();
		Logger.output(stackTraceElement, Level.WARNING, exception, null, new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(exception, logEntry.getException());

		Logger.output(stackTraceElement, Level.ERROR, exception, "Test", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level("com.test", Level.DEBUG).activate();

		Logger.output(stackTraceElement, Level.DEBUG, null, "Hello!", new Object[0]);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level("com.test", null).activate();

		Logger.output(stackTraceElement, Level.INFO, null, "Hello {}!", new Object[] { new EvilObject() });
		assertNull(writer.consumeLogEntry());
		assertEquals("LOGGER ERROR: Failed to create log entry (" + RuntimeException.class.getName() + ")", getErrorStream().nextLine());

		/* Test failure of writing log entry */

		Configurator.currentConfig().writer(new EvilWriter()).activate();

		Logger.output(stackTraceElement, Level.ERROR, null, "Hello!", new Object[0]);
		assertEquals("LOGGER ERROR: Failed to write log entry (" + UnsupportedOperationException.class.getName() + ")", getErrorStream().nextLine());

		/* Test using writing thread */

		Configurator.currentConfig().writer(writer).writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.output(stackTraceElement, Level.INFO, null, "Hello!", new Object[0]);
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

	/**
	 * Test output method with class name.
	 *
	 * @throws InterruptedException
	 *             Test failed
	 */
	@Test
	public final void testOutputWithClassName() throws InterruptedException {
		/* Test logging without writer */

		Configurator.defaultConfig().level(LoggerTest.class, Level.TRACE).writer(null).activate();
		Logger.info("Hello!");

		/* Initialize writer */

		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS);
		Configurator.defaultConfig().writer(writer).activate();

		/* Test logging of class */

		Configurator.currentConfig().level(Level.DEBUG).activate();

		Logger.info("Hello!");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());

		/* Test logging of plain texts */

		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE);
		Configurator.currentConfig().writer(writer).level(Level.INFO).activate();

		Logger.debug("Hello!");
		assertNull(writer.consumeLogEntry());

		Logger.info("Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		/* Test logging of exceptions */

		writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
		Configurator.currentConfig().writer(writer).level(Level.INFO).activate();

		Exception exception = new Exception();
		Logger.warn(exception, null);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(exception, logEntry.getException());

		Logger.error(exception, "Test");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Test", logEntry.getMessage());
		assertEquals(exception, logEntry.getException());

		/* Test different logging level of an particular package */

		Configurator.currentConfig().level(LoggerTest.class.getPackage(), Level.DEBUG).activate();

		Logger.debug("Hello!");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());

		/* Test failure of creating log entry */

		Configurator.currentConfig().level(LoggerTest.class.getPackage(), null).activate();

		Logger.info("Hello {}!", new EvilObject());
		assertNull(writer.consumeLogEntry());
		assertEquals("LOGGER ERROR: Failed to create log entry (" + RuntimeException.class.getName() + ")", getErrorStream().nextLine());

		/* Test failure of writing log entry */

		Configurator.currentConfig().writer(new EvilWriter(writer.getRequiredLogEntryValues())).activate();

		Logger.error("Hello!");
		assertEquals("LOGGER ERROR: Failed to write log entry (" + UnsupportedOperationException.class.getName() + ")", getErrorStream().nextLine());

		/* Test using writing thread */

		Configurator.currentConfig().writer(writer).writingThread(null).activate();
		WritingThread writingThread = findWritingThread();
		assertNotNull(writingThread);

		Logger.info("Hello!");
		assertNull(writer.consumeLogEntry());
		writingThread.shutdown();
		writingThread.join();
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello!", logEntry.getMessage());
	}

	/**
	 * Test output of log entries if writer accept only log entries of a defined severity level.
	 */
	@Test
	public final void testOutputIfWriterHasSeverityLevel() {
		StoreWriter writer = new StoreWriter(LogEntryValue.MESSAGE);
		Configurator.defaultConfig().writer(writer, Level.INFO).level(Level.TRACE).activate();

		Logger.trace(1);
		assertNull(writer.consumeLogEntry());
		Logger.debug(2);
		assertNull(writer.consumeLogEntry());
		Logger.info(3);
		assertNotNull(writer.consumeLogEntry());
		Logger.warn(4);
		assertNotNull(writer.consumeLogEntry());
		Logger.error(5);
		assertNotNull(writer.consumeLogEntry());
	}

	/**
	 * Test a log entry with a process ID pattern.
	 */
	@Test
	public final void testLogEntryWithProcessId() {
		StoreWriter writer = new StoreWriter(LogEntryValue.PROCESS_ID, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{pid}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(EnvironmentHelper.getProcessId(), logEntry.getProcessId());
		assertEquals(EnvironmentHelper.getProcessId() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a thread pattern.
	 */
	@Test
	public final void testLogEntryWithThread() {
		StoreWriter writer = new StoreWriter(LogEntryValue.THREAD, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{thread}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(Thread.currentThread().getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a thread ID pattern.
	 */
	@Test
	public final void testLogEntryWithThreadId() {
		StoreWriter writer = new StoreWriter(LogEntryValue.THREAD, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{thread_id}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(Thread.currentThread().getId() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a fully qualified class name pattern.
	 */
	@Test
	public final void testLogEntryWithFullyQualifiedClassName() {
		StoreWriter writer = new StoreWriter(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{class}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a package name pattern.
	 */
	@Test
	public final void testLogEntryWithPackageName() {
		StoreWriter writer = new StoreWriter(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{package}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getPackage().getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("com.test.MyClass", "unknown", "unknown", -1), Level.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("com.test.MyClass", logEntry.getClassName());
		assertEquals("com.test" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("MyClass", "unknown", "unknown", -1), Level.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a class name pattern.
	 */
	@Test
	public final void testLogEntryWithClassName() {
		StoreWriter writer = new StoreWriter(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{class_name}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getSimpleName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("com.test.MyClass", "unknown", "unknown", -1), Level.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("com.test.MyClass", logEntry.getClassName());
		assertEquals("MyClass" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Logger.output(new StackTraceElement("MyClass", "unknown", "unknown", -1), Level.INFO, null, "Hello", new Object[0]);

		logEntry = writer.consumeLogEntry();
		assertEquals("MyClass", logEntry.getClassName());
		assertEquals("MyClass" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a method name pattern.
	 */
	@Test
	public final void testLogEntryWithMethodName() {
		StoreWriter writer = new StoreWriter(LogEntryValue.METHOD, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{method}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("testLogEntryWithMethodName", logEntry.getMethodName());
		assertEquals("testLogEntryWithMethodName" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a file name pattern.
	 */
	@Test
	public final void testLogEntryWithFileName() {
		StoreWriter writer = new StoreWriter(LogEntryValue.FILE, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{file}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("LoggerTest.java", logEntry.getFilename());
		assertEquals("LoggerTest.java" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a line number pattern.
	 */
	@Test
	public final void testLogEntryWithLineNumber() {
		StoreWriter writer = new StoreWriter(LogEntryValue.LINE, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{line}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(lineNumber + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a logging level pattern.
	 */
	@Test
	public final void testLogEntryWithLoggingLevel() {
		StoreWriter writer = new StoreWriter(LogEntryValue.LINE, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{level}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Level.INFO + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a date pattern.
	 */
	@Test
	public final void testLogEntryWithDate() {
		StoreWriter writer = new StoreWriter(LogEntryValue.DATE, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{date:yyyy-MM-dd}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(ZonedDateTime.now().toEpochSecond(), logEntry.getDate().toEpochSecond(), 10d /* delta of 10 seconds */);
		assertEquals(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(ZonedDateTime.now()) + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test a log entry with a message pattern.
	 */
	@Test
	public final void testLogEntryWithMessage() {
		StoreWriter writer = new StoreWriter(LogEntryValue.MESSAGE, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{message}").activate();

		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals("Hello", logEntry.getMessage());
		assertEquals("Hello" + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());

		Exception exception = new Exception();
		Logger.info(exception, "Hello");

		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("Hello", logEntry.getMessage());
		assertThat(logEntry.getRenderedLogEntry(),
				allOf(startsWith("Hello"), containsString(exception.getClass().getName()), endsWith(EnvironmentHelper.getNewLine())));
	}

	/**
	 * Test a full log entry with all possible patterns.
	 */
	@Test
	public final void testFullLogEntry() {
		StoreWriter writer = new StoreWriter(EnumSet.allOf(LogEntryValue.class));
		Configurator.defaultConfig().writer(writer).level(Level.INFO)
		.formatPattern("{pid}#{thread}#{thread_id}#{class}#{package}#{class_name}#{method}#{file}#{line}#{level}#{date:yyyy}#{message}").activate();

		int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
		Logger.info("Hello");

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(EnvironmentHelper.getProcessId(), logEntry.getProcessId());
		assertEquals(Thread.currentThread(), logEntry.getThread());
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals("testFullLogEntry", logEntry.getMethodName());
		assertEquals("LoggerTest.java", logEntry.getFilename());
		assertEquals(lineNumber, logEntry.getLineNumber());
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(ZonedDateTime.now().toEpochSecond(), logEntry.getDate().toEpochSecond(), 10d /* delta of 10 seconds */);
		assertEquals("Hello", logEntry.getMessage());
		assertNull(logEntry.getException());

		String renderedLogEntry = MessageFormatter.format("{}#{}#{}#{}#{}#{}#testFullLogEntry#LoggerTest.java#{}#{}#{}#Hello{}",
				EnvironmentHelper.getProcessId(), Thread.currentThread().getName(), Thread.currentThread().getId(), LoggerTest.class.getName(),
				LoggerTest.class.getPackage().getName(), LoggerTest.class.getSimpleName(), lineNumber, Level.INFO,
				DateTimeFormatter.ofPattern("yyyy").format(ZonedDateTime.now()), EnvironmentHelper.getNewLine());
		assertEquals(renderedLogEntry, logEntry.getRenderedLogEntry());
	}

	/**
	 * Test outputting complex exceptions.
	 */
	@Test
	public final void testExceptions() {
		String newLine = EnvironmentHelper.getNewLine();
		StoreWriter writer = new StoreWriter(LogEntryValue.LEVEL, LogEntryValue.EXCEPTION, LogEntryValue.RENDERED_LOG_ENTRY);

		Configurator.defaultConfig().writer(writer).level(Level.ERROR).formatPattern("{message}").maxStackTraceElements(0).activate();

		Throwable exception = new Throwable();
		Logger.error(exception);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("java.lang.Throwable" + newLine, logEntry.getRenderedLogEntry());

		exception = new Throwable("Hello");
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertEquals("java.lang.Throwable: Hello" + newLine, logEntry.getRenderedLogEntry());

		Configurator.currentConfig().maxStackTraceElements(1).activate();

		exception = new Exception("Test");
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertThat(logEntry.getRenderedLogEntry(), matchesPattern("java\\.lang\\.Exception\\: Test" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine + "\t\\.\\.\\." + newLine));

		Configurator.currentConfig().maxStackTraceElements(-1).activate();

		exception = new RuntimeException(new NullPointerException());
		Logger.error(exception);
		logEntry = writer.consumeLogEntry();
		assertEquals(exception, logEntry.getException());
		assertThat(logEntry.getRenderedLogEntry(), containsPattern("java\\.lang\\.RuntimeException.*" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine));
		assertThat(logEntry.getRenderedLogEntry(), containsPattern("Caused by: java\\.lang\\.NullPointerException" + newLine
				+ "\tat org.pmw.tinylog.LoggerTest.testExceptions\\(LoggerTest.java:\\d*\\)" + newLine));
	}

	/**
	 * Test getting and setting configuration.
	 *
	 * @throws Exception
	 *             Test failed
	 */
	@Test
	public final void testConfiguration() throws Exception {
		/* Test getting and setting configuration */

		Configuration configuration = Configurator.defaultConfig().writer(null).formatPattern("Hello World").create();
		Logger.setConfiguration(configuration);
		assertEquals("Hello World", Logger.getConfiguration().create().getFormatPattern());

		/* Reset logger */

		Field field = Logger.class.getDeclaredField("configuration");
		field.setAccessible(true);
		field.set(null, null);

		/* Call init() method only once */

		DummyWriter writer = new DummyWriter();

		configuration = Configurator.defaultConfig().writer(writer).create();
		Logger.setConfiguration(configuration);
		assertThat(Logger.getConfiguration().create().getWriters(), sameContent(writer));
		assertEquals(1, writer.numberOfInits);

		configuration = Configurator.defaultConfig().writer(writer).create();
		Logger.setConfiguration(configuration);
		assertThat(Logger.getConfiguration().create().getWriters(), sameContent(writer));
		assertEquals(1, writer.numberOfInits);

		writer = new DummyWriter();

		assertEquals(0, writer.numberOfInits);
		configuration = Configurator.defaultConfig().writer(writer).create();
		Logger.setConfiguration(configuration);
		assertThat(Logger.getConfiguration().create().getWriters(), sameContent(writer));
		assertEquals(1, writer.numberOfInits);
	}

	/**
	 * Test if tinylog gets the right class name of caller even if calling of sun.reflect.Reflection will fail.
	 */
	@SuppressWarnings("restriction")
	@Test
	public final void testErrorWhileCallingSunReflection() {
		StoreWriter writer = new StoreWriter(LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{class}").activate();

		new MockUp<sun.reflect.Reflection>() {

			@Mock(invocations = 1)
			public Class<?> getCallerClass(final Invocation invocation, final int index) {
				try {
					throw new UnsupportedOperationException();
				} finally {
					tearDown();
				}
			}

		};

		Logger.info("Hello");

		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER WARNING\\: Failed to get caller class from sun.reflect.Reflection \\(.+\\)"));

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals(LoggerTest.class.getName() + EnvironmentHelper.getNewLine(), logEntry.getRenderedLogEntry());
	}

	/**
	 * Test if tinylog gets the right stack trace element even if getting single stack trace element will fail.
	 */
	@Test
	public final void testErrorWhileGettingSingleStackTraceElement() {
		StoreWriter writer = new StoreWriter(LogEntryValue.CLASS, LogEntryValue.METHOD, LogEntryValue.RENDERED_LOG_ENTRY);
		Configurator.defaultConfig().writer(writer).level(Level.INFO).formatPattern("{class}.{method}()").activate();

		new MockUp<Throwable>() {

			@Mock(invocations = 1)
			public StackTraceElement getStackTraceElement(final Invocation invocation, final int index) {
				try {
					throw new UnsupportedOperationException();
				} finally {
					tearDown();
				}
			}

		};

		Logger.info("Hello");

		assertThat(getErrorStream().nextLine(), matchesPattern("LOGGER WARNING\\: Failed to get single stack trace element from throwable \\(.+\\)"));

		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(LoggerTest.class.getName(), logEntry.getClassName());
		assertEquals("testErrorWhileGettingSingleStackTraceElement", logEntry.getMethodName());
		assertEquals(LoggerTest.class.getName() + ".testErrorWhileGettingSingleStackTraceElement()" + EnvironmentHelper.getNewLine(),
				logEntry.getRenderedLogEntry());
	}

	private static WritingThread findWritingThread() {
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			if (thread instanceof WritingThread) {
				return (WritingThread) thread;
			}
		}
		return null;
	}

	private static final class EvilObject {

		@Override
		public String toString() {
			throw new RuntimeException();
		}

	}

	private static final class EvilWriter extends NullWriter {

		public EvilWriter() {
			super();
		}

		public EvilWriter(final Set<LogEntryValue> requiredLogEntryValues) {
			super(requiredLogEntryValues);
		}

		@Override
		public void write(final LogEntry logEntry) {
			throw new UnsupportedOperationException();
		}

	}

	private static final class DummyWriter extends NullWriter {

		private int numberOfInits = 0;

		@Override
		public void init(final Configuration configuration) {
			++numberOfInits;
		}

	}

}

