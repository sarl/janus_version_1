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
import org.janusproject.ecoresolution.identity.EcoIdentity;


/** Event describing a change in the collection of dependencies against eco-entities.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class DependencyEvent extends AbstractEcoEntityEvent {
	
	private static final long serialVersionUID = 712167815997354587L;
	
	private final Collection<EcoIdentity> dependencies;
	private final EventType type;
	
	/**
	 * @param entity is the entity that has changed its dependencies.
	 * @param type is the type of event.
	 * @param dependencies are the dependencies in this event.
	 */
	public DependencyEvent(EcoEntity entity, EventType type, EcoIdentity... dependencies) {
		super(entity);
		assert(dependencies.length>0);
		this.type = type;
		this.dependencies = Arrays.asList(dependencies);
	}

	/**
	 * @param entity is the entity that has changed its dependencies.
	 * @param type is the type of event.
	 * @param dependencies are the dependencies in this event.
	 */
	public DependencyEvent(EcoEntity entity, EventType type, Collection<EcoIdentity> dependencies) {
		super(entity);
		this.type = type;
		this.dependencies = Collections.unmodifiableCollection(dependencies);
	}

	/** Replies the dependencies in this event.
	 * 
	 * @return the dependencies in this event.
	 */
	public Collection<EcoIdentity> getDependencies() {
		return this.dependencies;
	}
	
	/** Replies the type of action described by this event.
	 * 
	 * @return the event type.
	 */
	public EventType getType() {
		return this.type;
	}
	
}