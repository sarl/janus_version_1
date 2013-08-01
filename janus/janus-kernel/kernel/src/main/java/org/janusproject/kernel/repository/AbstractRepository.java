/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2013 Janus Core Developers
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

import java.util.Iterator;
import java.util.Map.Entry;

import org.janusproject.kernel.repository.RepositoryChangeEvent.ChangeType;
import org.janusproject.kernel.util.event.ListenerCollection;
import org.janusproject.kernel.util.sizediterator.ModifiableCollectionSizedIteratorOwner;
import org.janusproject.kernel.util.sizediterator.SizedIterator;


/** A object that permits to map an identifier to a set (eventually of size 1) of
 * data.
 *  
 * @param <ID> is the type of the discriminating identifier
 * @param <DATA> is the type of the target information
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractRepository<ID,DATA>
implements Repository<ID,DATA>, ModifiableCollectionSizedIteratorOwner<Entry<ID,DATA>> {

	private ListenerCollection<RepositoryChangeListener> listeners = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RepositoryOverlooker<ID> getOverlooker() {
		return new Overlooker();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void addRepositoryChangeListener(RepositoryChangeListener listener) {
		if (this.listeners==null)
			this.listeners = new ListenerCollection<RepositoryChangeListener>();
		this.listeners.add(RepositoryChangeListener.class, listener);		
	}

	/** {@inheritDoc}
	 */
	@Override
	public void removeRepositoryChangeListener(RepositoryChangeListener listener) {
		if (this.listeners==null) return;
		this.listeners.remove(RepositoryChangeListener.class,listener);
		if (this.listeners.isEmpty()) this.listeners = null;
	}

	/** Fire the event that indicates the specified object was added.
	 * 
	 * @param addedObject is the key of the added pair.
	 * @param objectValue is the object's value.
	 */
	public void fireAddRepositoryChange(ID addedObject, DATA objectValue){
		fireRepositoryChange(ChangeType.ADD, addedObject, null, objectValue);
	}
	
	/** Fire the event that indicates the specified object was removed.
	 * 
	 * @param removedObject is the key of the removed pair.
	 * @param oldValue is the object's value.
	 */
	public void fireRemoveRepositoryChange(ID removedObject, DATA oldValue) {
		fireRepositoryChange(ChangeType.REMOVE, removedObject, oldValue, null);
	}
	
	/** Fire the event that indicates the specified object was removed.
	 * 
	 * @param updatedObject is the key of the added pair.
	 * @param oldValue is the old object's value.
	 * @param newValue is the new object's value.
	 */
	public void fireUpdateRepositoryChange(ID updatedObject, DATA oldValue, DATA newValue) {
		fireRepositoryChange(ChangeType.UPDATE, updatedObject, oldValue, newValue);
	}

	/** Invoked to obtain the repository to pass to events.
	 * <p>
	 * By default, reply this.
	 * 
	 * @return the repository to passto events. 
	 */
	protected Repository<ID,DATA> getEventRepository() {
		return this;
	}
	
	/** Fire event.
	 */
	private void fireRepositoryChange(ChangeType type, ID change, DATA oldValue, DATA newValue) {
		if (this.listeners!=null) {
			RepositoryChangeEvent evt = new RepositoryChangeEvent(getEventRepository(),
					type,change,oldValue,newValue);
			for (RepositoryChangeListener l : this.listeners.getListeners(RepositoryChangeListener.class)) {
				l.repositoryChanged(evt);
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean contains(ID id) {
		return get(id) != null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean isEmpty() {
		return size() == 0;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Iterator<ID> iterator() {
		return new RepositoryIterator(getEntryIterator(), true);
	}
	
	/** {@inheritDoc} 
	 */
	@Override
	public SizedIterator<ID> sizedIterator() {
		return new RepositoryIterator(getEntryIterator(), true);
	}
	
	/** Replies the iterator on the repository entries.
	 * 
	 * @return the list of keys.
	 */
	protected abstract SizedIterator<Entry<ID,DATA>> getEntryIterator();

	/** Add an element in this repository.
	 * 
	 * @param key is the unique identifier.
	 * @param data is the associated data.
	 */
	public abstract void add(ID key, DATA data);

	/** Remove an element from this repository.
	 * 
	 * @param key is the unique identifier.
	 * @return the removed data.
	 */
	public abstract DATA remove(ID key);

	/** Clear this repository.
	 */
	public final void clear() {
		Iterator<Entry<ID,DATA>> iterator = getEntryIterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRemoveFromIterator(Entry<ID,DATA> data) {
		fireRemoveRepositoryChange(data.getKey(), data.getValue());
	}

	/** A object that permits to map an identifier to a set (eventually of size 1) of
	 * data.
	 *  
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RepositoryIterator implements SizedIterator<ID> {

		private final SizedIterator<? extends Entry<ID,?>> iterator;
		private final boolean modifiable;
		
		/**
		 * @param it
		 * @param modifiable
		 */
		public RepositoryIterator(SizedIterator<? extends Entry<ID,?>> it, boolean modifiable) {
			this.iterator = it;
			this.modifiable = modifiable;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int rest() {
			return this.iterator.rest();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int totalSize() {
			return this.iterator.totalSize();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ID next() {
			return this.iterator.next().getKey();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if (this.modifiable)
				this.iterator.remove();
			else
				throw new UnsupportedOperationException();
		}

	} /* class RepositoryIterator */
	
	/** 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Overlooker implements RepositoryOverlooker<ID> {

		/**
		 */
		public Overlooker() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void addRepositoryChangeListener(RepositoryChangeListener listener) {
			AbstractRepository.this.addRepositoryChangeListener(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeRepositoryChangeListener(RepositoryChangeListener listener) {
			AbstractRepository.this.removeRepositoryChangeListener(listener);
		}

		/** {@inheritDoc}
		 */
		@Override
		public Iterator<ID> iterator() {
			return new RepositoryIterator(getEntryIterator(), false);
		}
		
	}
	
}
