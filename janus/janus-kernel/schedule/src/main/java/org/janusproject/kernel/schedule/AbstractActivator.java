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

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.logging.Logger;

import org.janusproject.kernel.logger.LoggerProvider;

/**
 * Determine a sequential execution policy among a set of activable objects.
 * 
 * @param <A> is the type of the object to activate.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractActivator<A extends Activable>
extends AbstractActivableContainer<A> {

	private SoftReference<LoggerProvider> loggerProvider = null;
	
	/** 
	 * @param type is the type of supported activable objects.
	 */
	public AbstractActivator(Class<A> type) {
		super(type);
	}

	/**
	 * @param type is the type of supported activable objects.
	 * @param scheduledObjects is the list of scheduled activable objects.
	 */
	public AbstractActivator(Class<A> type, Collection<? extends A> scheduledObjects) {
		super(type, scheduledObjects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoggerProvider(LoggerProvider loggerProvider) {
		if (loggerProvider==null) this.loggerProvider = null;
		else this.loggerProvider = new SoftReference<LoggerProvider>(loggerProvider);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Logger getLogger() {
		LoggerProvider lp = (this.loggerProvider==null) ? null : this.loggerProvider.get();
		Logger l;
		if (lp==null) {
			l = Logger.getAnonymousLogger();
		}
		else {
			l = lp.getLogger();
		}
		return l;
	}
	
	/** Add an activable object.
	 * 
	 * @param activableObject is the activable object to add inside this activator. 
	 */
	protected void addActivableObject(A activableObject) {
		add(activableObject);
	}

	/** Add all activable objects.
	 * 
	 * @param activableObjects are the activable objects to add inside this activator. 
	 */
	protected void addAllActivableObjects(Collection<? extends A> activableObjects) {
		addAll(activableObjects);
	}

	/** Remove an activable object.
	 * 
	 * @param activableObject is the activable object to remove from this activator.
	 * @return <code>true</code> if the object was removed; otherwise <code>false</code>.
	 */
	protected boolean removeActivableObject(A activableObject) {
		return remove(activableObject);
	}

	/** Remove all activable objects.
	 */
	protected void removeAllActivableObjects() {
		removeAll();
	}

	/** Remove all given activable objects.
	 * 
	 * @param activableObjects are the activable objectsto remove from this activator. 
	 */
	protected void removeAllActivableObjects(Collection<? extends A> activableObjects) {
		removeAll(activableObjects);
	}

}
