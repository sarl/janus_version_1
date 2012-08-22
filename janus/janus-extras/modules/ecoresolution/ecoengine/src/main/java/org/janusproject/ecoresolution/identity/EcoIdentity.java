/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.ecoresolution.identity;

import java.io.Serializable;
import java.util.UUID;

/** Describes an entity which has an identity in eco-resolution problem solving.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class EcoIdentity implements Comparable<EcoIdentity>, Serializable {
	
	private static final long serialVersionUID = -1096914865807609257L;
	
	private final UUID id;
	
	/**
	 * @param id is the identifier of the entity.
	 */
	public EcoIdentity(UUID id) {
		this.id = id;
	}
	
	/**
	 * Create identity with random id.
	 */
	public EcoIdentity() {
		this.id = UUID.randomUUID();
	}

	/** Replies the identifier of the entity.
	 * @return the identifier of the entity.
	 */
	public final UUID getEcoEntityId() {
		return this.id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof EcoIdentity) {
			EcoIdentity e = (EcoIdentity)o;
			return this.id.equals(e.id);
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 31 + this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(EcoIdentity o) {
		if (o==null) return Integer.MAX_VALUE;
		return this.id.compareTo(o.id);
	}
	
	/** Replies a string-representation of this identity.
	 * 
	 * @return a string-representation of this identity.
	 */
	@Override
	public String toString() {
		return this.id.toString();
	}
	
}