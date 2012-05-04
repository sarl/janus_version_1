/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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

import java.io.StringWriter;

import junit.framework.TestCase;

import org.janusproject.groovyengine.exceptions.NotANormalizedDirectoryException;

/**
 * 
 * @author $Author: lcabasso$
 */
public class GroovyAgentTests extends TestCase {
	private static final String GROOVY_SCRIPTS_DIRECTORY_PATH = "org/janusproject/groovyengine"; //$NON-NLS-1$
	
	/**
	 * Tested agent
	 */
	GroovyAgent testedAgent;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	public void setUp() throws Exception {
		this.testedAgent = new GroovyAgent();
		
		GroovyDirectoryFinder dirFinder = new GroovyDirectoryFinder(GROOVY_SCRIPTS_DIRECTORY_PATH);
		this.testedAgent.setGroovyDirectory( dirFinder );
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#getScriptExecutor()}.
	 */
	public void testGetScriptExecutor() {
		assertNotNull(this.testedAgent.getScriptExecutor());
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#setScriptExecutor(org.janusproject.groovyengine.GroovyExecutionScriptContext)}.
	 */
	public void testSetScriptExecutor() {
		GroovyExecutionScriptContext context = new GroovyExecutionScriptContext();
		this.testedAgent.setScriptExecutor( context );
		assertSame( context, this.testedAgent.getScriptExecutor() );
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#setGroovyDirectory(org.janusproject.groovyengine.GroovyDirectoryFinder)}.
	 * @throws NotANormalizedDirectoryException 
	 * @throws NullPointerException 
	 */
	public void testSetGroovyDirectory() throws NullPointerException, NotANormalizedDirectoryException {
		GroovyDirectoryFinder dirFinder = new GroovyDirectoryFinder(GROOVY_SCRIPTS_DIRECTORY_PATH);
		this.testedAgent.setGroovyDirectory( dirFinder );
		assertSame( dirFinder, this.testedAgent.getGroovyDirectory() );
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runScriptFromPath(java.lang.String)}.
	 */
	public void testRunScriptFromPathString() {
		String s = this.testedAgent.runScriptFromPath("HelloWorld.gy"); //$NON-NLS-1$
		assertEquals("", s); //$NON-NLS-1$
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runScriptFromPath(java.lang.String, java.io.StringWriter, java.io.StringWriter)}.
	 */
	public void testRunScriptFromPathStringStringWriterStringWriter() {
		StringWriter errorOutput = new StringWriter();
		StringWriter standardOutput = new StringWriter();
		
		String s = this.testedAgent.runScriptFromPath("HelloWorld.gy", errorOutput, standardOutput); //$NON-NLS-1$

		assertEquals("", s); //$NON-NLS-1$
		assertEquals("", errorOutput.toString()); //$NON-NLS-1$
		assertEquals("hello world", standardOutput.toString()); //$NON-NLS-1$
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runGroovyCommand(java.lang.String)}.
	 */
	public void testRunGroovyCommandString() {
		String res = this.testedAgent.runGroovyCommand("5+5"); //$NON-NLS-1$
		assertEquals("10", res); //$NON-NLS-1$
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runGroovyCommand(java.lang.String, java.io.StringWriter, java.io.StringWriter)}.
	 */
	public void testRunGroovyCommandStringStringWriterStringWriter() {
		StringWriter errorOutput = new StringWriter();
		StringWriter standardOutput = new StringWriter();
		
		String s = this.testedAgent.runGroovyCommand("5+5", errorOutput, standardOutput); //$NON-NLS-1$
		
		assertEquals("10", s); //$NON-NLS-1$
		assertEquals("", errorOutput.toString()); //$NON-NLS-1$
		assertEquals("", standardOutput.toString()); //$NON-NLS-1$
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runGroovyFunction(java.lang.String, java.lang.String, java.lang.String, java.lang.Object[])}.
	 */
	public void testRunGroovyFunctionStringStringStringObjectArray() {
		Integer a = Integer.valueOf(5);
		Integer b = Integer.valueOf(10);
		Integer expectedReturn = Integer.valueOf(15);
		
		Object returnedObj = this.testedAgent.runGroovyFunction(GROOVY_SCRIPTS_DIRECTORY_PATH, "addition.gy", "add", a, b); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals( expectedReturn, returnedObj );
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runGroovyFunction(java.lang.String, java.lang.String, java.lang.Object[])}.
	 */
	public void testRunGroovyFunctionStringStringObjectArray() {
		Integer a = Integer.valueOf(5);
		Integer b = Integer.valueOf(10);
		Integer expectedReturn = Integer.valueOf(15);
		
		Object returnedObj = this.testedAgent.runGroovyFunction("addition.gy", "add", a, b); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals( expectedReturn, returnedObj );
	}

	/**
	 * Test method for {@link org.janusproject.groovyengine.GroovyAgent#runGroovyFunction(java.lang.String, java.lang.String, java.io.StringWriter, java.io.StringWriter, java.lang.Object[])}.
	 */
	public void testRunGroovyFunctionStringStringStringWriterStringWriterObjectArray() {
		StringWriter errorOutput = new StringWriter();
		StringWriter standardOutput = new StringWriter();
		
		Integer a = Integer.valueOf(5);
		Integer b = Integer.valueOf(10);
		Integer expectedReturn = Integer.valueOf(15);
		
		Object returnedObj = this.testedAgent.runGroovyFunction("addition.gy", "add", errorOutput, standardOutput, a, b); //$NON-NLS-1$ //$NON-NLS-2$
		
		assertEquals( expectedReturn, returnedObj );
		assertEquals("", errorOutput.toString()); //$NON-NLS-1$
		assertEquals("", standardOutput.toString()); //$NON-NLS-1$
	}

}
