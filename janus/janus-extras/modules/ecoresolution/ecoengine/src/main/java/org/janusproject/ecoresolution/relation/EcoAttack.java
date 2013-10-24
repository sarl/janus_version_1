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
package org.janusproject.ecoresolution.relation;

import java.io.Serializable;
import java.util.Collection;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.ecoresolution.identity.EcoIdentity;

/** Describes an attack of an eco-entity against another eco-entity
 * in eco-resolution problem solving.
 * <p>
 * An attack has an source of the attack, a defender, and a constraint.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class EcoAttack implements Serializable {

	private static final long serialVersionUID = 8423355477632530474L;
	
	private final EcoIdentity assailant;
	private final EcoIdentity defender;
	private final Collection<EcoRelation> constraints;
	private final Object[] parameters;
	
	/**
	 * @param assailant is the source of the attack.
	 * @param defender is the target of the attack.
	 * @param constraints are the constraints to be followed by the defender.
	 * @param parameters are additional parameters to pass to defender.
	 */
	public EcoAttack(EcoIdentity assailant, EcoIdentity defender, Collection<EcoRelation> constraints, Object... parameters) {
		assert(assailant!=null && defender!=null);
		this.assailant = assailant;
		this.defender = defender;
		this.constraints = constraints;
		this.parameters = parameters;
	}
	
	/**
	 * @param assailant is the source of the attack.
	 * @param defender is the target of the attack.
	 * @param parameters are additional parameters to pass to defender.
	 */
	public EcoAttack(EcoIdentity assailant, EcoIdentity defender, Object... parameters) {
		this.assailant = assailant;
		this.defender = defender;
		this.constraints = null;
		this.parameters = parameters;
	}
	
	/** Replies the assailant, ie. the source of the attack.
	 * 
	 * @return the assailant
	 */
	public EcoIdentity getAssailant() {
		return this.assailant;
	}

	/** Replies the defender, ie. the target of the attack.
	 * 
	 * @return the defender
	 */
	public EcoIdentity getDefender() {
		return this.defender;
	}

	/** Replies the constraint which must be followed by the defender.
	 * 
	 * @return the attack constraint.
	 */
	public Collection<EcoRelation> getConstraints() {
		return this.constraints;
	}
	
	/** Replies the additional parameters embedded in this attack.
	 * 
	 * @return the additional parameters.
	 */
	public Object[] getParameters() {
		return this.parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (this.constraints==null) {
			return Locale.getString(EcoAttack.class, "NO_CONSTRAINED_ATTACK", this.assailant, this.defender); //$NON-NLS-1$
		}
		return Locale.getString(EcoAttack.class, "CONSTRAINED_ATTACK", this.assailant, this.defender, this.constraints); //$NON-NLS-1$
	}

}