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
package org.janusproject.kernel.util.directaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * Stub.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ListenerStub implements AsynchronousThreadSafeCollectionListener<DataStub> {

	private final List<DataStub> addedObjects = new ArrayList<DataStub>();
	private final List<DataStub> removedObjects = new ArrayList<DataStub>();
	
	/**
	 */
	public ListenerStub() {
		//
	}
	
	/** Reset this stub.
	 */
	public void reset() {
		this.addedObjects.clear();
		this.removedObjects.clear();
	}

	/**
	 */
	public void assertNull() {
		if (!this.addedObjects.isEmpty()) {
			fail("Unexpected added elements:"+this.addedObjects.toString()); //$NON-NLS-1$
		}
		else if (!this.removedObjects.isEmpty()) {
			fail("Unexpected removed elements:"+this.removedObjects.toString()); //$NON-NLS-1$
		}
		else {
			reset();
		}
	}
	
	private static void fail(String s) {
		throw new AssertionFailedError(s);
	}

	/**
	 * @param expectedObject
	 */
	public void assertRemoved(Object expectedObject) {
		assert(expectedObject!=null);
		if (!this.removedObjects.remove(expectedObject)) {
			fail("removed object not found: "+expectedObject); //$NON-NLS-1$
		}
	}
	
	/**
	 * @param expectedObject
	 */
	public void assertAdded(Object expectedObject) {
		assert(expectedObject!=null);
		if (!this.addedObjects.remove(expectedObject)) {
			fail("added object not found: "+expectedObject); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void asynchronouslyAdded(Collection<? extends DataStub> added) {
		this.addedObjects.addAll(added);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void asynchronouslyRemoved(Collection<? extends DataStub> removed) {
		this.removedObjects.addAll(removed);
	}

}
