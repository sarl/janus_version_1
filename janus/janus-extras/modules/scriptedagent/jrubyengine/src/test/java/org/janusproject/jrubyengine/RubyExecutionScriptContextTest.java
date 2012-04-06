/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.jrubyengine;

import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * Unit Test for the RubyExecutionScriptContext 
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyExecutionScriptContextTest extends TestCase {

	private RubyExecutionScriptContext rubyExecutionContext;

	/**
	 * 
	 */
	public final void testGetEngine() {
		this.rubyExecutionContext = new RubyExecutionScriptContext();
	}

	/**
	 * 
	 */
	public final void testRunScriptFromPathString() {
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts/emptyScriptTest.rb")); //$NON-NLS-1$ //$NON-NLS-2$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts/putsTest.rb")); //$NON-NLS-1$ //$NON-NLS-2$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts/calcTest.rb")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public final void testRunScriptFromPathStringString() {
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts/", "emptyScriptTest.rb")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts/", "emptyScriptTest")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts", "emptyScriptTest.rb")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts", "emptyScriptTest")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("./src/test/ruby/scripts", "emptyScriptTest")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * 
	 */
	public final void testRunScriptFromPathStringStringWriterStringWriter() {
		StringWriter errors = new StringWriter();
		StringWriter puts = new StringWriter();
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts", "emptyScriptTest", errors, puts) + puts.getBuffer() + errors.getBuffer()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		errors = new StringWriter();
		puts = new StringWriter();
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("hello\n", this.rubyExecutionContext.runScriptFromPath("src/test/ruby/scripts", "putsTest", errors, puts) + puts.getBuffer() + errors.getBuffer()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * 
	 */
	public final void testRunRubyCommandString() {
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("", this.rubyExecutionContext.runRubyCommand("puts \"hello\"")); //$NON-NLS-1$; //$NON-NLS-2$
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("1", this.rubyExecutionContext.runRubyCommand("$a=1")); //$NON-NLS-1$; //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public final void testRunRubyCommandStringStringWriterStringWriter() {
		StringWriter errors = new StringWriter();
		StringWriter puts = new StringWriter();
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals("1", this.rubyExecutionContext.runRubyCommand("$a=1", errors, puts) + puts.getBuffer() + puts.getBuffer()); //$NON-NLS-1$; //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public final void testRunRubyFunctionStringStringObjectArray() {
		this.rubyExecutionContext = new RubyExecutionScriptContext();
		assertEquals(new Long(2), this.rubyExecutionContext.runRubyFunction("src/test/ruby/scripts/functionScript.rb", "func", 1)); //$NON-NLS-1$; //$NON-NLS-2$
	}

}
