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


import java.util.EventObject;

import org.janusproject.ecoresolution.entity.EcoEntity;

/** Event on the eco-entity.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEcoEntityEvent extends EventObject {
	
	private static final long serialVersionUID = 1450949687273264326L;

	/**
	 * @param entity is the entity that has changed its goal.
	 */
	public AbstractEcoEntityEvent(EcoEntity entity) {
		super(entity);
	}
	
	/** Replies the entity.
	 * 
	 * @return the entity.
	 */
	public EcoEntity getEcoEntity() {
		return (EcoEntity)getSource();
	}
	
}