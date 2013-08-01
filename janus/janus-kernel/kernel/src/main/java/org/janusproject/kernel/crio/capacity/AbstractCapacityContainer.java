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

import java.util.ArrayList;
import java.util.Collection;

import org.janusproject.kernel.repository.RepositoryChangeEvent;
import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.repository.RepositoryChangeListener;

/**
 * Stores the capacities of a given entity and their associated implementation.
 * <p>
 * The default capacity implementation selection policy is an instance of
 * {@link FirstCapacityImplementationSelectionPolicy}.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see Capacity
 */
public abstract class AbstractCapacityContainer
implements CapacityContainer {

	private Collection<RepositoryChangeListener> listeners = null;
	private CapacityImplementationSelectionPolicy capacitySelectionPolicy = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeRepositoryChangeListener(RepositoryChangeListener listener) {
		if (this.listeners!=null) {
			this.listeners.remove(listener);
			if (this.listeners.isEmpty())
				this.listeners = null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addRepositoryChangeListener(RepositoryChangeListener listener) {
		if (this.listeners==null)
			this.listeners = new ArrayList<RepositoryChangeListener>();
		this.listeners.add(listener);
	}
	
	/**
	 * Fire a repository change event.
	 * 
	 * @param event
	 */
	protected void fireRepositoryChangeEvent(RepositoryChangeEvent event) {
		if (this.listeners!=null) {
			for(RepositoryChangeListener listener : this.listeners) {
				listener.repositoryChanged(event);
			}
		}
	}

	/**
	 * Fire a repository addition event.
	 * 
	 * @param implementation is the added implementation.
	 */
	protected void fireRepositoryAddition(CapacityImplementation implementation) {
		RepositoryChangeEvent event = new RepositoryChangeEvent(
				this,
				ChangeType.ADD,
				implementation,
				null,
				implementation);
		fireRepositoryChangeEvent(event);
	}

	/**
	 * Fire a repository removal event.
	 * 
	 * @param implementation is the removed implementation.
	 */
	protected void fireRepositoryRemoval(CapacityImplementation implementation) {
		RepositoryChangeEvent event = new RepositoryChangeEvent(
				this,
				ChangeType.REMOVE,
				implementation,
				implementation,
				null);
		fireRepositoryChangeEvent(event);
	}
	
	/** Set the capacity implementation policy used by {@link #selectImplementation(Class)}.
	 *
	 * @param policy the new policy or <code>null</code> for default policy.
	 */
	public final void setCapacityImplementationSelectionPolicy(CapacityImplementationSelectionPolicy policy) {
		this.capacitySelectionPolicy = policy;
	}
	
	/** Replies the capacity implementation policy used by {@link #selectImplementation(Class)}.
	 *
	 * @return the policy or <code>null</code> for default policy.
	 */
	public final CapacityImplementationSelectionPolicy getCapacityImplementationSelectionPolicy() {
		return this.capacitySelectionPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <CI extends Capacity> CI selectImplementation(Class<CI> capacity) {
		assert(capacity!=null);
		Collection<? extends CapacityImplementation> implementations = get(capacity);
		if (implementations==null) return null;
		CapacityImplementationSelectionPolicy policy = getCapacityImplementationSelectionPolicy();
		if (policy==null) policy = FirstCapacityImplementationSelectionPolicy.DEFAULT;
		return capacity.cast(policy.selectImplementation(implementations));
	}

}
