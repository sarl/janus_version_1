/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import org.janusproject.kernel.util.sizediterator.SizedIterator;

/** A object that permits to map an identifier to a set (eventually of size 1) of
 * data.
 *  
 * @param <ID> is the type of the discriminating identifier
 * @param <TARGET> is the type of the target information
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Repository<ID,TARGET> extends Iterable<ID> {

	/** Replies the size of this repository
	 * 
	 * @return the size of the repository. 
	 */
	public int size();

	/** Replies the size of this repository
	 * 
	 * @return the size of the repository. 
	 */
	public SizedIterator<ID> sizedIterator();

	/** Replies if the specified id is iside this repository.
	 * 
	 * @param id is the key to test.
	 * @return <code>true</code> if the given id is a key inside this repository,
	 * otherwise <code>false</code>
	 */
	public boolean contains(ID id);

	/** Get the data associated to the specified key.
	 * 
	 * @param id is the key to test.
	 * @return the data or <code>null</code> if none inside.
	 */
	public TARGET get(ID id);

	/** Replies if the repository is empty.
	 * 
	 * @return <code>true</code> if this repository is empty,
	 * otherwise <code>false</code>
	 */
	public boolean isEmpty();
	
	/** Replies the list of the keys.
	 * 
	 * @return the list of the keys.
	 */
	public Collection<ID> identifiers();

	/** Replies the list of the values.
	 * 
	 * @return the list of the values.
	 */
	public Collection<TARGET> values();

	/** Add a listener on repository changes.
	 * 
	 * @param listener is the listener on repository changes. 
	 */
	public void addRepositoryChangeListener(RepositoryChangeListener listener);

	/** Remove a listener on repository changes.
	 * 
	 * @param listener is the listener on repository changes. 
	 */
	public void removeRepositoryChangeListener(RepositoryChangeListener listener);
	
	/** Replies the overlooker on this repository.
	 * 
	 * @return overlooker on this repository.
	 * @since 0.5
	 */
	public RepositoryOverlooker<ID> getOverlooker();

}
