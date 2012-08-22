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

/** Describes a relation between two eco-entit in eco-resolution problem solving.
 * <p>
 * This relation is asymetric. It make a relation between a master eco-entity
 * and a slave eco-entity.
 *
 * @param <ME> is the type of the instances of this eco-relation.
 * @param <INVERT> is the type of the invert instances of this eco-relation.
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEcoRelation<ME extends AbstractEcoRelation<? extends ME,? extends INVERT>, INVERT extends AbstractEcoRelation<? extends INVERT,? extends ME>> implements EcoRelation {
	
	private static final long serialVersionUID = -8597750346507511113L;
	
	private final EcoIdentity master;
	private final EcoIdentity slave;
	
	/**
	 * @param master
	 * @param slave
	 */
	public AbstractEcoRelation(EcoIdentity master, EcoIdentity slave) {
		this.master = master;
		this.slave = slave;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public EcoIdentity getMaster() {
		return this.master;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public EcoIdentity getSlave() {
		return this.slave;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public EcoIdentity getOtherParticipant(EcoIdentity participant) {
		if (this.master.equals(participant)) return this.slave;
		if (this.slave.equals(participant)) return this.master;
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object o) {
		if (o instanceof EcoRelation) {
			return equals((EcoRelation)o);
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract int hashCode();

	/** {@inheritDoc}
	 */
	@Override
	public abstract boolean equals(EcoRelation relation);
	
	/** {@inheritDoc}
	 */
	@Override
	public abstract INVERT invert();

}