/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.groovyengine;

import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import junit.framework.TestCase;

import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.afc.vmutil.Resources;
import org.janusproject.scriptedagent.ScriptErrorListener;

/**
 * @author $Author: sgalland$
 */
public class GroovyExecutionContextTest extends TestCase {

	private static final String ADDITION_SCRIPT_NAME = "addition.groovy"; //$NON-NLS-1$
	private static final String INTERPRETED_ADDITION_SCRIPT_NAME = "interpretedaddition.groovy"; //$NON-NLS-1$
	private static final String HELLO_WORLD_SCRIPT_NAME = "HelloWorld.groovy"; //$NON-NLS-1$
	private static final String MY_PRINT_SCRIPT_NAME = "Print.groovy"; //$NON-NLS-1$
	private static final String METHOD_SCRIPT_NAME = "Method.groovy"; //$NON-NLS-1$
	
	private GroovyExecutionContext interpreter;
	
	private URL additionScript;
	private File scriptDirectory;
	private Listener listener;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		this.additionScript = Resources.getResource(GroovyExecutionContext.class, ADDITION_SCRIPT_NAME);
		this.scriptDirectory = FileSystem.convertURLToFile(this.additionScript).getParentFile();
		this.interpreter = new GroovyExecutionContext(new ScriptEngineManager());
		this.listener = new Listener();
		this.interpreter.addScriptErrorListener(this.listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		this.additionScript = null;
		this.scriptDirectory = null;
		this.interpreter = null;
		this.listener = null;
		super.tearDown();
	}
	
	private File makeFile(String name) {
		return new File(this.scriptDirectory, name);
	}
	
	private URL makeURL(String name) throws Exception {
		File f = makeFile(name);
		return f.toURI().toURL();
	}

	/**
	 * @throws Exception
	 */
	public void testMakeFunctionCall() throws Exception {
		assertEquals("myFunc()", this.interpreter.makeFunctionCall("myFunc")); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertEquals("myFunc(1,3,\"ab\\\"c\")", //$NON-NLS-1$
				this.interpreter.makeFunctionCall("myFunc", 1, 3, "ab\"c")); //$NON-NLS-1$ //$NON-NLS-2$

		assertEquals("myFunc(1.5,2)", //$NON-NLS-1$
				this.interpreter.makeFunctionCall("myFunc", 1.5d, new BigDecimal(2))); //$NON-NLS-1$
		
		assertEquals("myFunc(null,3)",  //$NON-NLS-1$
				this.interpreter.makeFunctionCall("myFunc", null, 3)); //$NON-NLS-1$
	}
	
	/**
	 * @throws Exception
	 */
	public void testRunCommandString() throws Exception {
		Object v = this.interpreter.runCommand("null == null"); //$NON-NLS-1$
		assertTrue((Boolean)v);
		this.listener.assertFalse();

		v = this.interpreter.runCommand("5+6"); //$NON-NLS-1$
		assertEquals(11, v);
		this.listener.assertFalse();
		
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runCommand("6+6"); //$NON-NLS-1$
		assertEquals(12, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
		
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runCommand("print 7+10*2"); //$NON-NLS-1$
		assertNull(v);
		assertEquals("27", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @throws Exception
	 */
	public void testRunScriptString() throws Exception {
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runScript(ADDITION_SCRIPT_NAME);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(HELLO_WORLD_SCRIPT_NAME);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();
		
		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(INTERPRETED_ADDITION_SCRIPT_NAME);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.interpreter.getScriptRepository().addDirectory(this.scriptDirectory);

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(ADDITION_SCRIPT_NAME);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(HELLO_WORLD_SCRIPT_NAME);
		assertNull(v);
		assertEquals("hello world", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(INTERPRETED_ADDITION_SCRIPT_NAME);
		assertEquals(15, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @throws Exception
	 */
	public void testRunScriptFile() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runScript(makeFile(ADDITION_SCRIPT_NAME));
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(makeFile(HELLO_WORLD_SCRIPT_NAME));
		assertNull(v);
		assertEquals("hello world", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(makeFile(INTERPRETED_ADDITION_SCRIPT_NAME));
		assertEquals(15, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @throws Exception
	 */
	public void testRunScriptURL() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runScript(makeURL(ADDITION_SCRIPT_NAME));
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(makeURL(HELLO_WORLD_SCRIPT_NAME));
		assertNull(v);
		assertEquals("hello world", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runScript(makeURL(INTERPRETED_ADDITION_SCRIPT_NAME));
		assertEquals(15, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}
	
	/**
	 * @throws Exception
	 */
	public void testRunFunctionStringStringObjectArray() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runFunction(
				ADDITION_SCRIPT_NAME,
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.interpreter.getScriptRepository().addDirectory(this.scriptDirectory);

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				ADDITION_SCRIPT_NAME,
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				ADDITION_SCRIPT_NAME,
				"add", //$NON-NLS-1$
				18,
				27);
		assertEquals(45, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				MY_PRINT_SCRIPT_NAME,
				"myprint", //$NON-NLS-1$
				"ab\"c"); //$NON-NLS-1$
		assertNull(v);
		assertEquals("ab\"c", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @throws Exception
	 */
	public void testRunFunctionFileStringObjectArray() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runFunction(
				makeFile(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.interpreter.getScriptRepository().addDirectory(this.scriptDirectory);

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeFile(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeFile(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18,
				27);
		assertEquals(45, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeFile(MY_PRINT_SCRIPT_NAME),
				"myprint", //$NON-NLS-1$
				"ab\"c"); //$NON-NLS-1$
		assertNull(v);
		assertEquals("ab\"c", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @throws Exception
	 */
	public void testRunFunctionURLStringObjectArray() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runFunction(
				makeURL(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.interpreter.getScriptRepository().addDirectory(this.scriptDirectory);

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeURL(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18);
		assertNull(v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertTrue();

		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeURL(ADDITION_SCRIPT_NAME),
				"add", //$NON-NLS-1$
				18,
				27);
		assertEquals(45, v);
		assertEquals("", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
		
		this.listener.reset();
		output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		v = this.interpreter.runFunction(
				makeURL(MY_PRINT_SCRIPT_NAME),
				"myprint", //$NON-NLS-1$
				"ab\"c"); //$NON-NLS-1$
		assertNull(v);
		assertEquals("ab\"c", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();		
	}
	
	/**
	 * @throws Exception
	 */
	public void testMethodInvocation() throws Exception {
		this.listener.reset();
		StringWriter output = new StringWriter();
		this.interpreter.setStandardOutput(output);
		Object v = this.interpreter.runFunction(
				makeURL(METHOD_SCRIPT_NAME),
				"printMyMsg", //$NON-NLS-1$
				new TestObject(" was printed"), //$NON-NLS-1$
				"my message"); //$NON-NLS-1$
		assertNull(v);
		assertEquals("my message was printed", output.toString()); //$NON-NLS-1$
		this.listener.assertFalse();
	}

	/**
	 * @author $Author: sgalland$
	 */
	private static class Listener implements ScriptErrorListener {

		/**
		 * Exception received?
		 */
		private final AtomicBoolean exception = new AtomicBoolean();
		
		/**
		 */
		public Listener() {
			//
		}
		
		/**
		 * Reset the listener.
		 */
		public void reset() {
			this.exception.set(false);
		}

		/**
		 * Assert that the listener was notified.
		 */
		public void assertTrue() {
			if (!this.exception.get())
				fail("Error listener not notified"); //$NON-NLS-1$
		}

		/**
		 * Assert that the listener was not notified.
		 */
		public void assertFalse() {
			if (this.exception.get())
				fail("Error listener notified"); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onScriptError(ScriptException e) {
			this.exception.set(true);
		}
		
	}
	
}
