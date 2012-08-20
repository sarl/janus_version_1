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
import org.janusproject.ecoresolution.relation.EcoRelation;


/** Event describing a change of acquaintance.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class AcquaintanceEvent extends AbstractEcoEntityEvent {
	
	private static final long serialVersionUID = -144221083514812007L;
	
	private final Collection<EcoRelation> acquaintances;
	private final EventType type;
	
	/**
	 * @param entity is the entity that has changed its acquaintances.
	 * @param type is the type of event.
	 * @param acquaintances are the acquaintances in this event.
	 */
	public AcquaintanceEvent(EcoEntity entity, EventType type, EcoRelation... acquaintances) {
		super(entity);
		assert(acquaintances.length>0);
		this.type = type;
		this.acquaintances = Arrays.asList(acquaintances);
	}

	/**
	 * @param entity is the entity that has changed its acquaintances.
	 * @param type is the type of event.
	 * @param acquaintances are the acquaintances in this event.
	 */
	public AcquaintanceEvent(EcoEntity entity, EventType type, Collection<EcoRelation> acquaintances) {
		super(entity);
		this.type = type;
		this.acquaintances = Collections.unmodifiableCollection(acquaintances);
	}

	/** Replies the acquaintance in this event.
	 * 
	 * @return the acquaintance in this event.
	 */
	public Collection<EcoRelation> getAcquaintances() {
		return this.acquaintances;
	}
	
	/** Replies the type of action described by this event.
	 * 
	 * @return the event type.
	 */
	public EventType getType() {
		return this.type;
	}
	
}