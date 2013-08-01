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
package org.janusproject.kernel.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class RepositoryChangeListenerStub implements RepositoryChangeListener {

	private final List<RepositoryChangeEvent> lastEvents = new ArrayList<RepositoryChangeEvent>();
	
	/**
	 */
	public RepositoryChangeListenerStub() {
		//
	}
	
	/** Reset this stub.
	 */
	public void reset() {
		this.lastEvents.clear();
	}

	/**
	 */
	public void assertNull() {
		if (this.lastEvents.isEmpty()) {
			reset();
			return;
		}
		fail("Unexpected event"); //$NON-NLS-1$
	}
	
	private static void fail(String s) {
		throw new AssertionFailedError(s);
	}

	/**
	 * @param source
	 * @param expectedType
	 * @param changedObject
	 * @param expectedOldValue
	 * @param expectedNewValue
	 */
	public void assertEquals(Object source, ChangeType expectedType, Object changedObject, Object expectedOldValue, Object expectedNewValue) {
		if (this.lastEvents.size()<=1) {
			RepositoryChangeEvent evt = this.lastEvents.isEmpty() ? null : this.lastEvents.remove(0);
			if (evt==null) {
				fail("expecting RepositoryChangeEvent"); //$NON-NLS-1$
			}
			else if (expectedType!=evt.getType()) {
				fail("expected type of RepositoryChangeEvent: "+expectedType+"; actual: "+evt.getType());  //$NON-NLS-1$//$NON-NLS-2$
			}
			else if (source!=evt.getSource()) {
				fail("expected source of RepositoryChangeEvent: "+source+"; actual: "+evt.getSource());  //$NON-NLS-1$//$NON-NLS-2$
			}
			else if (changedObject!=evt.getChangedObject()) {
				fail("expected changed object in RepositoryChangeEvent: "+changedObject+"; actual: "+evt.getChangedObject());  //$NON-NLS-1$//$NON-NLS-2$
			}
			else if (expectedOldValue!=evt.getOldValue()) {
				fail("expected old value in RepositoryChangeEvent: "+expectedOldValue+"; actual: "+evt.getOldValue());  //$NON-NLS-1$//$NON-NLS-2$
			}
			else if (expectedNewValue!=evt.getNewValue()) {
				fail("expected new value in RepositoryChangeEvent: "+expectedNewValue+"; actual: "+evt.getNewValue());  //$NON-NLS-1$//$NON-NLS-2$
			}
			reset();
		}
		else {
			Iterator<RepositoryChangeEvent> iterator = this.lastEvents.iterator();
			RepositoryChangeEvent evt = null;
			RepositoryChangeEvent e;
			while (iterator.hasNext() && evt==null) {
				e = iterator.next();
				if (e!=null
					&& expectedType==e.getType()
					&& source==e.getSource()
					&& changedObject==e.getChangedObject()
					&& expectedOldValue==e.getOldValue()
					&& expectedNewValue==e.getNewValue()) {
					evt = e;
					iterator.remove();
				}
			}
			if (evt==null) fail("event not found"); //$NON-NLS-1$
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repositoryChanged(RepositoryChangeEvent evt) {
		this.lastEvents.add(evt);
	}
	
}
