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
package org.janusproject.ecoresolution.event;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.janusproject.ecoresolution.entity.EcoEntity;
import org.janusproject.ecoresolution.relation.EcoAttack;


/** Event describing a change in the collection of eco-attacks.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class AttackEvent extends AbstractEcoEntityEvent {
	
	private static final long serialVersionUID = 3823051882657520499L;
	
	private final Collection<EcoAttack> attacks;
	private final EventType type;
	
	/**
	 * @param entity is the entity that has changed its attacks.
	 * @param type is the type of event.
	 * @param attacks are the attacks in this event.
	 */
	public AttackEvent(EcoEntity entity, EventType type, EcoAttack... attacks) {
		super(entity);
		assert(attacks.length>0);
		this.type = type;
		this.attacks = Arrays.asList(attacks);
	}

	/**
	 * @param entity is the entity that has changed its attacks.
	 * @param type is the type of event.
	 * @param attacks are the attacks in this event.
	 */
	public AttackEvent(EcoEntity entity, EventType type, Collection<EcoAttack> attacks) {
		super(entity);
		this.type = type;
		this.attacks = Collections.unmodifiableCollection(attacks);
	}

	/** Replies the attacks in this event.
	 * 
	 * @return the attacks in this event.
	 */
	public Collection<EcoAttack> getAttacks() {
		return this.attacks;
	}
	
	/** Replies the type of action described by this event.
	 * 
	 * @return the event type.
	 */
	public EventType getType() {
		return this.type;
	}
	
}