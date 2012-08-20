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
package org.janusproject.ecoresolution.relation;

import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.EcoRelation;

/** Utilities for relations.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class EcoRelationUtil {

	/**
	 * Replies the hashcode for the given relation.
	 * 
	 * @param type
	 * @param master
	 * @param slave
	 * @return the hashcode for the given relation.
	 */
	public static int hashCode(Class<? extends EcoRelation> type, EcoIdentity master, EcoIdentity slave) {
		int h = 1;
		h = h * 31 + ((master!=null) ? master.hashCode() : 0);
		h = h * 31 + ((slave!=null) ? slave.hashCode() : 0);
		h = h * 31 + ((type!=null) ? type.hashCode() : 0);
		return h;
	}

}