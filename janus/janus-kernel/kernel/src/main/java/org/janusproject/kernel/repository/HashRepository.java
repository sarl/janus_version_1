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
package org.janusproject.kernel.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.janusproject.kernel.util.sizediterator.ModifiableCollectionSizedIterator;
import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** A {@link HashMap}-based implementation of a Repository.
 *  
 * @param <ID> is the type of the discriminating identifier
 * @param <DATA> is the type of the target information
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class HashRepository<ID,DATA>
extends AbstractRepository<ID,DATA> {

	private final HashMap<ID,DATA> content; 

	/**
	 */
	public HashRepository() {
		this.content = new HashMap<ID,DATA>();
	}
	
	/**
     * @param initialCapacity is the initial capacity.
	 */
	public HashRepository(int initialCapacity) {
		this.content = new HashMap<ID,DATA>(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SizedIterator<Entry<ID,DATA>> getEntryIterator() {
		return new ModifiableCollectionSizedIterator<Entry<ID,DATA>>(
				this.content.entrySet(),
				this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DATA get(ID id) {
		return this.content.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(ID key, DATA data) {
		DATA oldData = this.content.put(key, data);
		if (oldData==null)
			fireAddRepositoryChange(key, data);
		else
			fireUpdateRepositoryChange(key, oldData, data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DATA remove(ID key) {
		DATA data = this.content.remove(key);
		if (data!=null)
			fireRemoveRepositoryChange(key, data);
		return data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.content.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<ID> identifiers() {
		return Collections.unmodifiableSet(this.content.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<DATA> values() {
		return Collections.unmodifiableCollection(this.content.values());
	}

}
