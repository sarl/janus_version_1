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

import java.util.EventObject;

/** Describe an change event on a repository.
 *
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RepositoryChangeEvent extends EventObject {

	private static final long serialVersionUID = 6604853042869392718L;

	/** Type of event on a repository.
	 * 
	 * @author $Author: srodriguez$
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum ChangeType {
		/**
		 * Indicates that an object was added to a repository.
		 */
		ADD,
		/**
		 * Indicates that an object was removed from a repository.
		 */
		REMOVE,
		/**
		 * Indicates that an object has changed inside a repository.
		 */
		UPDATE;
	}

	private final Object object;

	private final Object oldValue, newValue;

	private final ChangeType type;


	/**
	 * @param repository is the repository in which the event has occured
	 * @param type is the type of event
	 * @param changedObject is the added/removed/changed object
	 * @param oldValue is the old value of the object, or <code>null</code>
	 * @param newValue is the new value of the object, or <code>null</code>
	 */
	public RepositoryChangeEvent(Repository<?,?> repository, ChangeType type, Object changedObject, Object oldValue, Object newValue) {
		super(repository);
		this.type = type;
		this.object = changedObject;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * @return the repository in which this event has occured.
	 */
	@Override
	public Repository<?,?> getSource() {
		return (Repository<?,?>)super.getSource();
	}

	/**
	 * @return the type of this event.
	 */
	public ChangeType getType(){
		return this.type;
	}

	/**
	 * @return the object for which this event occurs.
	 */
	public Object getChangedObject(){
		return this.object;
	}

	/**
	 * @return the value of the object before the event occurs, or <code>null</code>.
	 */
	public Object getOldValue(){
		return this.oldValue;
	}

	/**
	 * @return the value of the object after the event occurs, or <code>null</code>.
	 */
	public Object getNewValue(){
		return this.newValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RepositoryEvent "+getSource().toString()+" "+this.type.toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
