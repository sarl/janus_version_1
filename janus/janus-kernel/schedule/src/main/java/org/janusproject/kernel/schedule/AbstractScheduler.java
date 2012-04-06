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
import org.janusproject.kernel.schedule.Activator;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/**
 * Determine a sequential execution policy among a set of activators.
 * 
 * @param <A> is the type of the object to activate.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractScheduler<A extends Activator<?>>
extends AbstractActivableContainer<A>
implements Scheduler<A> {

	private SoftReference<LoggerProvider> loggerProvider = null;
	private boolean isHierarchyUsed = false;
	
	/** 
	 * @param type is the type of supported activable objects.
	 */
	public AbstractScheduler(Class<A> type) {
		super(type);
	}

	/**
	 * @param type is the type of supported activable objects.
	 * @param scheduledObjects is the list of scheduled activable objects.
	 */
	public AbstractScheduler(Class<A> type, Collection<? extends A> scheduledObjects) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addActivator(A activator) {
		add(activator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllActivators(Collection<? extends A> activators) {
		addAll(activators);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeActivator(A activator) {
		remove(activator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllActivators() {
		removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllActivators(Collection<? extends A> activators) {
		removeAll(activators);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasActivable() {
		if (super.hasActivable()) {
			SafeIterator<A> iterator = iterator();
			A elt;
			try {
				while (iterator.hasNext()) {
					elt = iterator.next();
					if (elt.hasActivable())
						return true;
				}
			}
			finally {
				iterator.release();
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUsed() {
		if (this.isHierarchyUsed) return true;
		if (super.isUsed()) {
			SafeIterator<A> iterator = iterator();
			A elt;
			try {
				while (iterator.hasNext()) {
					elt = iterator.next();
					if (elt.isUsed()) {
						this.isHierarchyUsed = true;
						return true;
					}
				}
			}
			finally {
				iterator.release();
			}
		}
		return false;
	}

}
