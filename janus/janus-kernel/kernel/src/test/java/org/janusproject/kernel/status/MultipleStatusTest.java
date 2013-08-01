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

import java.util.Collections;
import java.util.List;
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
public class MultipleStatusTest extends TestCase {
    
	private Random rnd;
	private StatusSeverity severity;
	private String provider;
	private String message;
	private int code;
	private Throwable exception;
	private MultipleStatus status;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.rnd = new Random();
		this.severity = randomSeverity();
		this.code = randomCode(0);
		this.provider = UUID.randomUUID().toString();
		this.message = UUID.randomUUID().toString();
		this.exception = new Error();
		this.status = new MultipleStatus(
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
	
	private StatusSeverity randomSeverity() {
		StatusSeverity r = StatusSeverity.values()[this.rnd.nextInt(StatusSeverity.values().length)];
		while (r==StatusSeverity.OK || r==StatusSeverity.INFO) {
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
	public void testGetCode() {
		assertEquals(this.code, this.status.getCode());
	}

	/**
	 */
	public void testGetProviderID() {
		assertSame(this.provider, this.status.getProviderID());
	}

	/**
	 */
	public void testGetException() {
		assertSame(this.exception, this.status.getException());
	}

	/**
	 */
	public void testGetMessage() {
		assertSame(this.message, this.status.getMessage());
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
	public void testgetInnerStatus() {
		assertNotNull(this.status.getInnerStatus());
	}

	/**
	 */
	public void testAddStatusStatusSeverityStringIntStringThrowable() {
		Status s1 = new SingleStatus(this.severity, this.provider, this.code, this.message, this.exception);
		
		List<Status> list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(s1, list.get(0));
		
		Status s2 = new SingleStatus(this.severity, this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s2.getSeverity(), s2.getProviderID(), s2.getCode(), s2.getMessage(), s2.getException());

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));

		Status s3 = new SingleStatus(StatusSeverity.values()[this.severity.ordinal()-1], this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s3.getSeverity(), s3.getProviderID(), s3.getCode(), s3.getMessage(), s3.getException());

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));
		assertEquals(s3, list.get(2));
	}
	
	/**
	 */
	public void testAddStatusStatusArray() {
		Status s1 = new SingleStatus(this.severity, this.provider, this.code, this.message, this.exception);
		
		List<Status> list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(s1, list.get(0));
		
		Status s2 = new SingleStatus(this.severity, this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s2);

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));

		Status s3 = new SingleStatus(StatusSeverity.values()[this.severity.ordinal()-1], this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s3);

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));
		assertEquals(s3, list.get(2));
	}

	/**
	 */
	public void testAddStatusCollection() {
		Status s1 = new SingleStatus(this.severity, this.provider, this.code, this.message, this.exception);
		
		List<Status> list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(s1, list.get(0));
		
		Status s2 = new SingleStatus(this.severity, this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(Collections.singleton(s2));

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));

		Status s3 = new SingleStatus(StatusSeverity.values()[this.severity.ordinal()-1], this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(Collections.singleton(s3));

		list = this.status.getInnerStatus();
		
		assertNotNull(list);
		assertEquals(3, list.size());
		assertEquals(s2, list.get(0));
		assertEquals(s1, list.get(1));
		assertEquals(s3, list.get(2));
	}

    /**
     */
	public void testGetHigherStatus() {
		Status s1 = new SingleStatus(this.severity, this.provider, this.code, this.message, this.exception);
		
		assertEquals(s1, this.status.getHigherStatus());
		
		Status s2 = new SingleStatus(this.severity, this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s2);

		assertEquals(s2, this.status.getHigherStatus());

		Status s3 = new SingleStatus(StatusSeverity.values()[this.severity.ordinal()-1], this.provider, randomCode(this.code), this.message, this.exception);
		this.status.addStatus(s3);

		assertEquals(s2, this.status.getHigherStatus());
	}
	
	/**
	 */
	public void testIsEmpty() {
		assertFalse(this.status.isEmpty());
		this.status.clear();
		assertTrue(this.status.isEmpty());
	}

	/**
	 */
	public void testClear() {
		assertFalse(this.status.isEmpty());
		this.status.clear();
		assertTrue(this.status.isEmpty());
	}

    /**
     */
    public void testPackString() {
    	Status s;
    	
    	s = this.status.pack("toto"); //$NON-NLS-1$
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(this.severity, s.getSeverity());
    	assertEquals(this.message, s.getMessage());
    	assertEquals(this.code, s.getCode());
    	assertEquals(this.provider, s.getProviderID());
    	
    	this.status.addStatus(this.severity, this.provider, this.code, this.message, null);

    	assertSame(this.status, this.status.pack("toto")); //$NON-NLS-1$

    	this.status.clear();

    	s = this.status.pack("toto"); //$NON-NLS-1$
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(StatusSeverity.OK, s.getSeverity());
    	assertNull(s.getMessage());
    	assertEquals(KernelStatusConstants.SUCCESS, s.getCode());
    	assertEquals("toto", s.getProviderID()); //$NON-NLS-1$
    }
    
    /**
     */
    public void testPackClass() {
    	Status s;
    	
    	s = this.status.pack(getClass());
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(this.severity, s.getSeverity());
    	assertEquals(this.message, s.getMessage());
    	assertEquals(this.code, s.getCode());
    	assertEquals(this.provider, s.getProviderID());
    	
    	this.status.addStatus(this.severity, this.provider, this.code, this.message, null);

    	assertSame(this.status, this.status.pack(getClass()));

    	this.status.clear();

    	s = this.status.pack(getClass());
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(StatusSeverity.OK, s.getSeverity());
    	assertNull(s.getMessage());
    	assertEquals(KernelStatusConstants.SUCCESS, s.getCode());
    	assertEquals(getClass().getCanonicalName(), s.getProviderID());
    }

    /**
     */
    public void testPackObject() {
    	Status s;
    	
    	s = this.status.pack(this);
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(this.severity, s.getSeverity());
    	assertEquals(this.message, s.getMessage());
    	assertEquals(this.code, s.getCode());
    	assertEquals(this.provider, s.getProviderID());
    	
    	this.status.addStatus(this.severity, this.provider, this.code, this.message, null);

    	assertSame(this.status, this.status.pack(this));

    	this.status.clear();

    	s = this.status.pack(this);
    	assertNotNull(s);
    	assertTrue(s instanceof SingleStatus);
    	assertEquals(StatusSeverity.OK, s.getSeverity());
    	assertNull(s.getMessage());
    	assertEquals(KernelStatusConstants.SUCCESS, s.getCode());
    	assertEquals(Integer.toString(System.identityHashCode(this)), s.getProviderID());
    }

}
