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

import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;

/** Implementation of EcoRelation participant which stands for
 * all the instances of EcoEntity. 
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class AnyIdentity extends EcoIdentity {
	
	private static final long serialVersionUID = 1520341678594778936L;

	/** Singleton.
	 */
	public static final AnyIdentity SINGLETON = new AnyIdentity();
	
	/** Mask which is matching all the EcoEntity ids.
	 */
	public static final String ANY_ENTITY_MASK = ("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"); //$NON-NLS-1$

	private AnyIdentity() {
		super(UUID.fromString(ANY_ENTITY_MASK));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		return (o instanceof EcoIdentity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return ANY_ENTITY_MASK.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Locale.getString(AnyIdentity.class, "NAME"); //$NON-NLS-1$
	}
	
}