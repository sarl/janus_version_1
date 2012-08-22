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
package org.janusproject.ecoresolution.message;

import org.janusproject.ecoresolution.relation.EcoAttack;
import org.janusproject.kernel.message.AbstractContentMessage;

/** An attack message between two eco-agents or two eco-roles.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public final class EcoAttackMessage extends AbstractContentMessage<EcoAttack> {
	
	private static final long serialVersionUID = 6337181667784438031L;
	
	private final EcoAttack attack;
	
	/**
	 * @param attack
	 */
	public EcoAttackMessage(EcoAttack attack) {
		this.attack = attack;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EcoAttack getContent() {
		return this.attack;
	}
	
}