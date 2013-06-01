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
package org.janusproject.kernel.crio.capacity;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.janusproject.kernel.repository.RepositoryChangeEvent;
import org.janusproject.kernel.repository.RepositoryChangeListener;
import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.repository.RepositoryOverlooker;
import org.janusproject.kernel.logger.LoggerUtil;
import org.janusproject.kernel.util.sizediterator.EmptyIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/**
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AbstractCapacityContainerTest extends TestCase {

	private AbstractCapacityContainer container;
	private RepositoryChangeListenerStub listener;
	
	/**
	 * @throws Exception
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		LoggerUtil.setGlobalLevel(Level.OFF);
		this.container = new AbstractCapacityContainerStub();
		this.listener = new RepositoryChangeListenerStub();
		this.container.addRepositoryChangeListener(this.listener);
	}
	
	/**
	 * @throws Exception
	 */
	@Override
	public void tearDown() throws Exception {
		this.container = null;
		this.listener = null;
		super.tearDown();
	}

	/**
	 */
	public void testFireRepositoryChangeEventRepositoryChangeEvent() {
		assertNull(this.listener.event);

		RepositoryChangeEvent event = new RepositoryChangeEvent(
				this.container, ChangeType.ADD, new Object(), null, new Object());
		
		this.container.fireRepositoryChangeEvent(event);
		assertSame(event, this.listener.event);
	}

	/**
	 */
	public void testFireRepositoryAdditionCapacityImplementation() {
		assertNull(this.listener.event);

		CapacityImplementationStub c1 = new CapacityImplementationStub();
		this.container.fireRepositoryAddition(c1);
		
		assertNotNull(this.listener.event);
		assertSame(this.container, this.listener.event.getSource());
		assertSame(ChangeType.ADD, this.listener.event.getType());
		assertSame(c1, this.listener.event.getChangedObject());
		assertNull(this.listener.event.getOldValue());
		assertSame(c1, this.listener.event.getNewValue());
	}

	/**
	 */
	public void testFireRepositoryRemovalCapacityImplementation() {
		assertNull(this.listener.event);

		CapacityImplementationStub c1 = new CapacityImplementationStub();
		this.container.fireRepositoryRemoval(c1);
		
		assertNotNull(this.listener.event);
		assertSame(this.container, this.listener.event.getSource());
		assertSame(ChangeType.REMOVE, this.listener.event.getType());
		assertSame(c1, this.listener.event.getChangedObject());
		assertSame(c1, this.listener.event.getOldValue());
		assertNull(this.listener.event.getNewValue());
	}
	
	/**
	 */
	public void testRemoveRepositoryChangeListenerRepositoryChangeListener() {
		this.container.removeRepositoryChangeListener(this.listener);
		
		assertNull(this.listener.event);

		RepositoryChangeEvent event = new RepositoryChangeEvent(
				this.container, ChangeType.ADD, new Object(), null, new Object());
		
		this.container.fireRepositoryChangeEvent(event);
		assertNull(this.listener.event);
	}

	/**
	 */
	public void testSetCapacityImplementationSelectionPolicyCapacityImplementationSelectionPolicy() {
		assertNull(this.container.getCapacityImplementationSelectionPolicy());
		CapacityImplementationSelectionPolicy policy = RandomCapacityImplementationSelectionPolicy.DEFAULT;
		this.container.setCapacityImplementationSelectionPolicy(policy);
		assertSame(policy, this.container.getCapacityImplementationSelectionPolicy());
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AbstractCapacityContainerStub extends AbstractCapacityContainer {

		/**
		 */
		public AbstractCapacityContainerStub() {
			//
		}
		
		@Override
		public void addAll(CapacityContainer container) {
			//
		}

		@Override
		public void addCapacity(CapacityImplementation capacity) {
			//
		}

		@Override
		public void removeCapacity(Class<? extends Capacity> capacity) {
			//
		}

		@Override
		public void removeCapacity(CapacityImplementation capacity) {
			//
		}

		@Override
		public boolean contains(Class<? extends Capacity> id) {
			return false;
		}

		@Override
		public Collection<CapacityImplementation> get(Class<? extends Capacity> id) {
			return Collections.emptyList();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Iterator<Class<? extends Capacity>> iterator() {
			return EmptyIterator.singleton();
		}

		@Override
		public SizedIterator<Class<? extends Capacity>> sizedIterator() {
			return EmptyIterator.singleton();
		}

		@Override
		public Collection<Class<? extends Capacity>> identifiers() {
			return Collections.emptyList();
		}
		
		@Override
		public Collection<Collection<CapacityImplementation>> values() {
			return Collections.emptyList();
		}

		/** {@inheritDoc}
		 */
		@Override
		public RepositoryOverlooker<Class<? extends Capacity>> getOverlooker() {
			throw new UnsupportedOperationException();
		}

	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RepositoryChangeListenerStub implements RepositoryChangeListener {

		public RepositoryChangeEvent event = null;
		
		/**
		 */
		public RepositoryChangeListenerStub() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void repositoryChanged(RepositoryChangeEvent evt) {
			this.event = evt;
		}
		
	}
	
}
