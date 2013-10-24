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
package org.janusproject.demos.ecoresolution.ecocube.relation;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.ecoresolution.identity.AnyIdentity;
import org.janusproject.ecoresolution.identity.EcoIdentity;
import org.janusproject.ecoresolution.relation.AbstractEcoRelation;
import org.janusproject.ecoresolution.relation.EcoRelation;

/** Relationship "Master is below Slave"
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class UpwardRelation extends AbstractEcoRelation<UpwardRelation,DownwardRelation> {
	
	private static final long serialVersionUID = -7762744779590828355L;

	private final EcoIdentity table;
	
	/**
	 * @param master
	 * @param slave
	 * @param table
	 */
	public UpwardRelation(EcoIdentity master, EcoIdentity slave, EcoIdentity table) {
		super(master, slave);
		this.table = table;
	}

	/** Replies if this eco-relation is equals to the given eco-relation.
	 * 
	 * @param relation
	 * @return <code>true</code> if master, slave, and relation type are
	 * the same for both this eco-relation and the given eco-relation.
	 * Otherwise replies <code>false</code>.
	 */
	@Override
	public boolean equals(EcoRelation relation) {
		return CubeRelationUtil.isDownwardEqual(getSlave(), getMaster(), relation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return CubeRelationUtil.hashCode(this);
	}

	/** Replies the inverse relationship.
	 * 
	 * @return the inverse relationship.
	 */
	@Override
	public DownwardRelation invert() {
		return new DownwardRelation(getSlave(), getMaster(), this.table);
	}
	
	/** Replies if the given relation is under conflict with this object.
	 * 
	 * @param relation
	 * @return <code>true</code> if conflicting, otherwise <code>false</code>.
	 */
	@Override
	public boolean isConflict(EcoRelation relation) {
		return CubeRelationUtil.isConflict(this.table, this, relation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoRelation toPattern(EcoIdentity participantToRemove) {
		assert(participantToRemove!=null);
		EcoIdentity m = getMaster();
		EcoIdentity s = getSlave();
		if (m.equals(participantToRemove)) {
			return new UpwardRelation(AnyIdentity.SINGLETON, s, this.table);
		}
		if (s.equals(participantToRemove)) {
			return new UpwardRelation(m, AnyIdentity.SINGLETON, this.table);
		}
		return this;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Locale.getString(UpwardRelation.class, "TEXT", getMaster(), getSlave()); //$NON-NLS-1$
	}

}