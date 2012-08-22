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

import java.io.Serializable;

import org.janusproject.ecoresolution.identity.EcoIdentity;

/** Describes a relation between two eco-entit in eco-resolution problem solving.
 * <p>
 * This relation is asymetric. It make a relation between a master eco-entity
 * and a slave eco-entity.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public interface EcoRelation extends Serializable {
	
	/** Replies the master of this relationship.
	 * 
	 * @return the master in the relation.
	 */
	public EcoIdentity getMaster();
	
	/** Replies the slave of this relationship.
	 * 
	 * @return the slave in the relation.
	 */
	public EcoIdentity getSlave();
	
	/** Replies if this eco-relation is equals to the given eco-relation.
	 * 
	 * @param relation
	 * @return <code>true</code> if master, slave, and relation type are
	 * the same for both this eco-relation and the given eco-relation.
	 * Otherwise replies <code>false</code>.
	 */
	public boolean equals(EcoRelation relation);
	
	/** Replies the inverse relationship.
	 * 
	 * @return the inverse relationship.
	 */
	public EcoRelation invert();

	/** Replies if the given relation is under conflict with this object.
	 * 
	 * @param relation
	 * @return <code>true</code> if conflicting, otherwise <code>false</code>.
	 */
	public boolean isConflict(EcoRelation relation);

	/** Replies a eco-relation pattern which is corresponding to this eco-relation
	 * in which the given participant is removed.
	 * <p>
	 * A pattern is an eco-relation in which one of the participant is replaced by the wild card "any".
	 * 
	 * @param participantToRemove is the participant to remove to obtain the pattern.
	 * @return the pattern.
	 */
	public EcoRelation toPattern(EcoIdentity participantToRemove);

	/** Replies the participant to this relation, which is not the given one.
	 * 
	 * @param participant
	 * @return the other participant to this relation..
	 */
	public EcoIdentity getOtherParticipant(EcoIdentity participant);

}