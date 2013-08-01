/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.schedule;

import java.util.Collection;
import java.util.Iterator;

import org.janusproject.kernel.schedule.ActivationStage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.directaccess.AsynchronousThreadSafeCollection;
import org.janusproject.kernel.util.directaccess.AsynchronousThreadSafeCollectionListener;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/**
 * Provides an abstract implementation of activable container.
 * 
 * @param <A> is the type of the object to activate.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractActivableContainer<A extends Activable>
implements Activator<A> {

	private AsynchronousThreadSafeCollection<A> activeObjects;
	private final AsynchronousThreadSafeCollectionListener<A> activeObjectListener
		= new AsynchronousThreadSafeCollectionListener<A>() {
			@Override
			public void asynchronouslyAdded(Collection<? extends A> added) {
				assert(added!=null && !added.isEmpty());
				Iterator<? extends A> iterator = getExecutionPolicy(
						ActivationStage.INITIALIZATION,
						added);
				Status s = executeInit(iterator, getInitParameters());
				if (s!=null && s.isLoggable()) s.logOn(getLogger());
			}
			@Override
			public void asynchronouslyRemoved(Collection<? extends A> removed) {
				assert(removed!=null && !removed.isEmpty());
				Iterator<? extends A> iterator = getExecutionPolicy(
						ActivationStage.DESTRUCTION,
						removed);
				Status s = executeDestroy(iterator);
				if (s!=null && s.isLoggable()) s.logOn(getLogger());
			}		
	};
	private Object[] initParameters = null;
	private boolean isInit = false;
	private boolean isUsed;

	/** 
	 * @param type is the type of supported activable objects.
	 */
	public AbstractActivableContainer(Class<A> type) {
		this.activeObjects = new AsynchronousThreadSafeCollection<A>(type);
		this.activeObjects.setAutoApplyEnabled(false);
		this.isUsed = false;
	}

	/**
	 * @param type is the type of supported activable objects.
	 * @param scheduledObjects is the list of scheduled activable objects.
	 */
	public AbstractActivableContainer(Class<A> type, Collection<? extends A> scheduledObjects) {
		assert(type!=null);
		assert(scheduledObjects!=null);
		this.activeObjects = new AsynchronousThreadSafeCollection<A>(type);
		this.activeObjects.setAutoApplyEnabled(false);
		this.isUsed = !scheduledObjects.isEmpty();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void sync() {
		this.activeObjects.applyChanges(false);
	}

	/**
	 * {@inheritDoc}
     */
	@Override
	public int toArray(A[] a) {
		assert(a.length>=this.activeObjects.size());
		this.activeObjects.toArray(a);
		return this.activeObjects.size();
	}
	
	/**
	 * {@inheritDoc}
     */
	@Override
	public int size() {
		return this.activeObjects.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasActivable() {
		return !this.activeObjects.isEmpty() || this.activeObjects.hasPendingElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUsed() {
		return this.isUsed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void used() {
		this.isUsed = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unused() {
		this.isUsed = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canActivate(Class<?> type) {
		Class<A> t = this.activeObjects.getElementType();
		assert(t!=null);
		return type!=null && t.isAssignableFrom(type);
	}

	/**
	 * Replies a iterator of the given candidates for the
	 * given stage execution.
	 * 
	 * @param stage is the execution stage for which an iterator is mandatory.
	 * @param candidates are the available candidates for execution.
	 * @return an iterator, never <code>null</code>.
	 * @see #getExecutionPolicy(ActivationStage, Collection)
	 */
	protected abstract SafeIterator<A> getExecutionPolicy(
			ActivationStage stage,
			DirectAccessCollection<A> candidates);

	/**
	 * Replies a iterator of the given candidates for the
	 * given stage execution.
	 * 
	 * @param stage is the execution stage for which an iterator is mandatory.
	 * @param candidates are the available candidates for execution.
	 * @return an iterator, never <code>null</code>.
	 * @see #getExecutionPolicy(ActivationStage, DirectAccessCollection)
	 */
	protected abstract Iterator<? extends A> getExecutionPolicy(
			ActivationStage stage,
			Collection<? extends A> candidates);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status activate(Object... parameters) {
		this.initParameters = parameters;
		
		this.activeObjects.addAsynchronousThreadSafeCollectionListener(this.activeObjectListener);
		
		Status s;
		if (!this.activeObjects.isEmpty()) {
			SafeIterator<A> iterator = getExecutionPolicy(
					ActivationStage.INITIALIZATION,
					this.activeObjects);
			assert(iterator!=null);
			iterator.attachCollection(this.activeObjects);
			try {
				s = executeInit(iterator, parameters);
			}
			finally {
				iterator.release();
			}
		}
		else {
			s = StatusFactory.ok(this);
		}
		
		this.isInit = true;
		
		return s;
	}

	/**
	 * Activate init functions.
	 * 
	 * @param objects are the objects to activate.
	 * @param parameters are the parameters to pass to init.
	 * @return the initialization status.
	 */
	protected abstract Status executeInit(Iterator<? extends A> objects, Object... parameters);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status live() {
		Status s;
		
		if (!this.activeObjects.isEmpty()) {
			SafeIterator<A> iterator = getExecutionPolicy(
					ActivationStage.LIVE,
					this.activeObjects);
			assert(iterator!=null);
			iterator.attachCollection(this.activeObjects);
			try {
				s = executeBehaviour(iterator);
			}
			finally {
				iterator.release();
			}
		}
		else {
			s = StatusFactory.ok(this);
		}

		return s;
	}

	/**
	 * Activate behaviour functions.
	 * 
	 * @param objects are the objects to activate.
	 * @return the behaviour status.
	 */
	protected abstract Status executeBehaviour(Iterator<? extends A> objects);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Status end() {
		Status s;
		
		if (!this.activeObjects.isEmpty()) {
			SafeIterator<A> iterator = getExecutionPolicy(
					ActivationStage.DESTRUCTION,
					this.activeObjects);
			assert(iterator!=null);
			iterator.attachCollection(this.activeObjects);
			try {
				s = executeDestroy(iterator);
			}
			finally {
				iterator.release();
			}
		}
		else {
			s = StatusFactory.ok(this);
		}
		
		this.activeObjects.clear();
		this.activeObjects.applyChanges(false);
		this.activeObjects.removeAsynchronousThreadSafeCollectionListener(this.activeObjectListener);
		this.initParameters = null;
		return s;
	}
		
	/**
	 * Activate destroy functions.
	 * 
	 * @param objects are the objects to activate.
	 * @return the destruction status.
	 */
	protected abstract Status executeDestroy(Iterator<? extends A> objects);

	/** Replies the parameters to pass to init functions.
	 * 
	 * @return the parameters or <code>null</code>.
	 */
	protected Object[] getInitParameters() {
		return this.initParameters;
	}
	
	/** Replies an iterator on the currently activated objects.
	 * 
	 * @return an iterator on activated objects.
	 */
	SafeIterator<A> iterator() {
		assert(this.activeObjects!=null);
		return this.activeObjects.iterator();
	}
	
	/** Add an activable object.
	 * 
	 * @param activableObject is the activable object to add inside this activator. 
	 */
	final void add(A activableObject) {
		assert(activableObject!=null);
		used();
		this.activeObjects.add(activableObject);
	}

	/** Add all activable objects.
	 * 
	 * @param activableObjects are the activable objects to add inside this activator. 
	 */
	final void addAll(Collection<? extends A> activableObjects) {
		assert(activableObjects!=null);
		if (activableObjects.isEmpty()) return;
		used();
		this.activeObjects.addAll(activableObjects);
	}

	/** Remove an activable object.
	 * 
	 * @param activableObject is the activable object to remove from this activator.
	 * @return <code>true</code> if the object was removed; <code>false</code> otherwise. 
	 */
	final boolean remove(A activableObject) {
		assert(activableObject!=null);
		return this.activeObjects.remove(activableObject);
	}

	/** Remove all activable objects.
	 */
	final void removeAll() {
		this.activeObjects.clear();
	}

	/** Remove all given activable objects.
	 * 
	 * @param activableObjects are the activable objectsto remove from this activator. 
	 */
	final void removeAll(Collection<? extends A> activableObjects) {
		assert(activableObjects!=null);
		this.activeObjects.removeAll(activableObjects);
	}
	
	/** Replies if this activator was init.
	 * 
	 * @return activator initialization.
	 */
	public boolean isInit() {
		return this.isInit;
	}
	
}
