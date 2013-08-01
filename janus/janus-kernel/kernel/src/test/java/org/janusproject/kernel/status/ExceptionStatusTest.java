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
package org.janusproject.kernel.status;

import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ExceptionStatusTest extends TestCase {

	private Throwable exception;
	private ExceptionStatus status;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.exception = new Error();
		this.status = new ExceptionStatus(this.exception);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.status = null;
		this.exception = null;
		super.tearDown();
	}
	
	/**
	 */
	public void testGetSeverity() {
		assertSame(StatusSeverity.ERROR, this.status.getSeverity());
	}

	/**
	 */
	public void testGetCode() {
		assertEquals(KernelStatusConstants.ERROR, this.status.getCode());
	}

	/**
	 */
	public void testGetProviderID() {
		assertEquals(this.exception.getStackTrace()[0].getClassName(), this.status.getProviderID());
	}

	/**
	 */
	public void testGetException() {
		assertSame(this.exception, this.status.getException());
	}

	/**
	 */
	public void testGetMessage() {
		assertEquals(this.exception.getLocalizedMessage(), this.status.getMessage());
	}

	/**
	 */
	public void testIsSuccess() {
		assertFalse(this.status.isSuccess());
	}

	/**
	 */
	public void testIsFailure() {
		assertTrue(this.status.isFailure());
	}

}
