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
 * @deprecated see {@link CreationDateMessageComparator}
 */
@Deprecated
public class MessageComparator
extends CreationDateMessageComparator {

	/**
	 */
	protected MessageComparator() {
		//
	}
		
}
