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

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.janusproject.kernel.logger.LoggerUtil;
import junit.framework.TestCase;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SingleStatusTest extends TestCase {

	private Random rnd;
	private StatusSeverity severity;
	private String provider;
	private String message;
	private int code;
	private Throwable exception;
	private SingleStatus status;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.rnd = new Random();
		this.severity = randomSeverity(null);
		this.code = randomCode(0);
		this.provider = UUID.randomUUID().toString();
		this.message = UUID.randomUUID().toString();
		this.exception = new Error();
		this.status = new SingleStatus(
				this.severity, 
				this.provider,
				this.code,
				this.message,
				this.exception);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tearDown() throws Exception {
		this.rnd = null;
		this.status = null;
		this.severity = null;
		this.provider = null;
		this.message = null;
		this.exception = null;
		super.tearDown();
	}
	
	private StatusSeverity randomSeverity(StatusSeverity s) {
		StatusSeverity r = StatusSeverity.values()[this.rnd.nextInt(StatusSeverity.values().length)];
		while (r==s) {
			r = StatusSeverity.values()[this.rnd.nextInt(StatusSeverity.values().length)];
		}
		return r;
	}

	private int randomCode(int c) {
		int r = this.rnd.nextInt();
		while (r==c) {
			r = this.rnd.nextInt();
		}
		return r;
	}

	/**
	 */
	public void testGetSeverity() {
		assertSame(this.severity, this.status.getSeverity());
	}

	/**
	 */
	public void testSetSeverityStatusSeverity() {
		StatusSeverity s = randomSeverity(this.severity);
		this.status.setSeverity(s);
		assertSame(s, this.status.getSeverity());
	}
	
	/**
	 */
	public void testGetCode() {
		assertEquals(this.code, this.status.getCode());
	}

	/**
	 */
	public void testSetCodeInt() {
		int c = randomCode(this.code);
		this.status.setCode(c);
		assertEquals(c, this.status.getCode());
	}

	/**
	 */
	public void testGetProviderID() {
		assertSame(this.provider, this.status.getProviderID());
	}

	/**
	 */
	public void testSetProviderIDString() {
		String p = UUID.randomUUID().toString();
		this.status.setProviderId(p);
		assertEquals(p, this.status.getProviderID());
	}

	/**
	 */
	public void testGetException() {
		assertSame(this.exception, this.status.getException());
	}

	/**
	 */
	public void testSetExceptionThrowable() {
		Throwable e = new Error();
		this.status.setException(e);
		assertSame(e, this.status.getException());
	}

	/**
	 */
	public void testGetMessage() {
		assertSame(this.message, this.status.getMessage());
	}

	/**
	 */
	public void testSetMessageString() {
		String m = UUID.randomUUID().toString();
		this.status.setMessage(m);
		assertEquals(m, this.status.getMessage());
	}

	/**
	 */
	public void testIsSuccess() {
		assertEquals(this.severity.isSuccess(), this.status.isSuccess());
	}

	/**
	 */
	public void testIsFailure() {
		assertEquals(this.severity.isFailure(), this.status.isFailure());
	}

	/**
	 */
	public void testMatchesStatusSeverity() {
		for(StatusSeverity severity : StatusSeverity.values()) {
			assertEquals(
					severity.name()+"("+this.severity.getMask()+";"+severity.getMask()+")",   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					this.severity==severity, this.status.matches(severity));
		}
	}

	/**
	 */
	public void testMatchesInt() {
		for(StatusSeverity severity : StatusSeverity.values()) {
			int s = severity.getMask() | 0x1000;
			assertEquals(severity.name()+"("+this.severity.getMask()+";"+severity.getMask()+")",   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					this.severity==severity, this.status.matches(s));
		}
	}

	/**
	 */
    public void testEqualsObject() {
        assertTrue(this.status.equals(this.status));
		SingleStatus s = new SingleStatus(
				this.severity, 
				this.provider,
				this.code,
				this.message,
				this.exception);
        assertTrue(this.status.equals(s));
        assertTrue(s.equals(this.status));

		s = new SingleStatus(
				randomSeverity(this.severity), 
				this.provider,
				this.code,
				this.message,
				this.exception);
        assertFalse(this.status.equals(s));
        assertFalse(s.equals(this.status));

		s = new SingleStatus(
				this.severity, 
				UUID.randomUUID().toString(),
				this.code,
				this.message,
				this.exception);
        assertFalse(this.status.equals(s));
        assertFalse(s.equals(this.status));

		s = new SingleStatus(
				this.severity, 
				this.provider,
				randomCode(this.code),
				this.message,
				this.exception);
        assertFalse(this.status.equals(s));
        assertFalse(s.equals(this.status));
    }

    /**
     */
    public void testHashCode() {
        assertEquals(this.status.hashCode(), this.status.hashCode());
		SingleStatus s = new SingleStatus(
				this.severity, 
				this.provider,
				this.code,
				this.message,
				this.exception);
        assertEquals(this.status.hashCode(), s.hashCode());
    }
    
    /**
     */
    public void testPackString() {
    	assertSame(this.status, this.status.pack("toto")); //$NON-NLS-1$
    }
    
    /**
     */
    public void testPackClass() {
    	assertSame(this.status, this.status.pack(getClass()));
    }

    /**
     */
    public void testPackObject() {
    	assertSame(this.status, this.status.pack(this));
    }

}
