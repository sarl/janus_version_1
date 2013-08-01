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

import org.janusproject.kernel.status.ExceptionStatus;
import org.janusproject.kernel.status.MultipleStatus;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.util.directaccess.DirectAccessCollection;
import org.janusproject.kernel.util.directaccess.SafeIterator;

/**
 * Determine a sequential execution policy among a set of activable objects.
 * 
 * @param <A> is the type of the object to activate.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DefaultActivator<A extends Activable>
extends AbstractActivator<A> {

	/** 
	 * @param type is the type of supported activable objects.
	 */
	public DefaultActivator(Class<A> type) {
		super(type);
	}

	/**
	 * @param type is the type of supported activable objects.
	 * @param scheduledObjects is the list of scheduled activable objects.
	 */
	public DefaultActivator(Class<A> type, Collection<? extends A> scheduledObjects) {
		super(type, scheduledObjects);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeInit(Iterator<? extends A> objects, Object... parameters) {
		MultipleStatus ms = new MultipleStatus();
		while (objects.hasNext()) {
			try {
				ms.addStatus(objects.next().activate(parameters));
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				ms.addStatus(new ExceptionStatus(e));
			}
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeBehaviour(Iterator<? extends A> objects) {
		MultipleStatus ms = new MultipleStatus();
		while (objects.hasNext()) {
			try {
				ms.addStatus(objects.next().live());
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				ms.addStatus(new ExceptionStatus(e));
			}
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Status executeDestroy(Iterator<? extends A> objects) {
		MultipleStatus ms = new MultipleStatus();
		while (objects.hasNext()) {
			try {
				ms.addStatus(objects.next().end());
			}
			catch(AssertionError e) {
				throw e;
			}
			catch(Throwable e) {
				ms.addStatus(new ExceptionStatus(e));
			}
		}
		return ms.pack(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SafeIterator<A> getExecutionPolicy(ActivationStage stage,
			DirectAccessCollection<A> candidates) {
		return candidates.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<? extends A> getExecutionPolicy(ActivationStage stage,
			Collection<? extends A> candidates) {
		return candidates.iterator();
	}

}
