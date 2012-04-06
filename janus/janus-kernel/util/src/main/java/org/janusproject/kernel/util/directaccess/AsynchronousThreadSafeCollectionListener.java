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
package org.janusproject.kernel.util.directaccess;

import java.util.Collection;
import java.util.EventListener;

/**
 * Listener on asynchronous thread-safe collection.
 * 
 * @param <E> is the type of the collection elements.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface AsynchronousThreadSafeCollectionListener<E>
extends EventListener {

	/** Invoked when objects are added inside the asynchronous
	 * thread-safe collection.
	 * 
	 * @param added is the collection of added objects.
	 */
	public void asynchronouslyAdded(Collection<? extends E> added);
	
	/** Invoked when objects are removed from the asynchronous
	 * thread-safe collection.
	 * 
	 * @param removed is the collection of removed objects.
	 */
	public void asynchronouslyRemoved(Collection<? extends E> removed);

}
