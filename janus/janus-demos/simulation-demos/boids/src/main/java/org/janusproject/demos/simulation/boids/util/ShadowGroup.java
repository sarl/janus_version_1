/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2010, 2012 Janus Core Developers
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
package org.janusproject.demos.simulation.boids.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.janusproject.kernel.address.AgentAddress;

/**
 * Group of shadows
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ShadowGroup {

	private final Collection<AgentAddress> members;
	private final Vector2f position = new Vector2f();
	private final Vector2f orientation = new Vector2f();
	
	/**
	 * @param groupSize
	 * @param groupPosition
	 * @param groupOrientation
	 * @param bodies
	 * @param group
	 */
	public ShadowGroup(
			int groupSize,
			Vector2f groupPosition, 
			Vector2f groupOrientation, 
			Map<AgentAddress,PerceivedBoidBody> bodies,
			Population group) {
		assert(groupSize>=1);
		this.members = new ArrayList<AgentAddress>();
		this.position.set(groupPosition);
		this.orientation.set(groupOrientation);
		
		PerceivedShadowBody body;
		AgentAddress adr;
		Vector2f boidRelativePosition;
		Vector2f boidPosition;
		Random rnd = new Random();
		
		for(int i=0; i<groupSize; ++i) {
			adr = new ShadowAddress();
			
			boidRelativePosition = new Vector2f(
					rnd.nextDouble() - rnd.nextDouble(),
					rnd.nextDouble() - rnd.nextDouble());
			boidRelativePosition.normalize();
			boidRelativePosition.scale((float)rnd.nextDouble() * Settings.SHADOW_GROUP_RADIUS);
			boidPosition = new Vector2f();
			boidPosition.add(this.position,boidRelativePosition);
			
			body = new PerceivedShadowBody(
					group,
					adr,
					boidPosition,
					new Vector2f(this.orientation),
					boidRelativePosition);
			
			this.members.add(adr);
			bodies.put(adr, body);
		}
	}
	
	/**
	 * @param groupPosition
	 * @param groupOrientation
	 * @param bodies
	 */
	public synchronized void setGroupPosition(Vector2f groupPosition, Vector2f groupOrientation, Map<AgentAddress,PerceivedBoidBody> bodies) {
		PerceivedShadowBody body;
		Vector2f newPos;
		for(AgentAddress member : this.members) {
			body = (PerceivedShadowBody)bodies.get(member);
			if (body!=null) {
				body.setOrientation(groupOrientation);
				newPos = new Vector2f();
				newPos.add(groupPosition, body.getPositionInShadowGroup());
				body.setPosition(newPos);
			}
		}
	}
	
	/**
	 * @param bodies
	 */
	public synchronized void clear(Map<AgentAddress,PerceivedBoidBody> bodies) {
		for(AgentAddress adr : this.members) {
			bodies.remove(adr);
		}
		this.members.clear();
	}

	/**
	 * @return the count of members in the group.
	 */
	public synchronized int size() {
		return this.members.size();
	}
	
	/**
	 * Group of shadows
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ShadowAddress extends AgentAddress {

		private static final long serialVersionUID = 6342514342432147486L;

		/**
		 */
		public ShadowAddress() {
			super(UUID.randomUUID(), null);
		}
		
	}
	
}
