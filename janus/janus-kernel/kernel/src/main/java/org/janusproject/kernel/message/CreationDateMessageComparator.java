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
package org.janusproject.kernel.message;

import java.util.Comparator;
import java.util.UUID;

import org.janusproject.kernel.message.Message;

/**
 * Comparator of mails based on their creation date.
 * <p>
 * According to the mailbox standards, when a message should be added,
 * the comparator is invoked as: <code>comparator(newObject, objectAlreadyInCollection)</code>.
 * It means that the first parameter given to the comparator is always the
 * parameter of the addition function.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CreationDateMessageComparator
implements Comparator<Message> {

	/** Singleton of a message comparator.
	 * 
	 * @since 0.5
	 */
	public static final CreationDateMessageComparator SINGLETON = new CreationDateMessageComparator();
	
	/**
	 */
	protected CreationDateMessageComparator() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Message o1, Message o2) {
		assert(o1!=null);
		assert(o2!=null);
		
		int cmp = Float.compare(o1.getCreationDate(), o2.getCreationDate());
		if (cmp!=0) return cmp;
		
		UUID id1 = o1.getIdentifier();
		UUID id2 = o2.getIdentifier();
		assert(id1!=null);
		assert(id2!=null);
		
		return id1.compareTo(id2);
	}
	
}
