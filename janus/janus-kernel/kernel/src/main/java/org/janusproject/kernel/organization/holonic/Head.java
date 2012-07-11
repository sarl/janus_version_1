/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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
package org.janusproject.kernel.organization.holonic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.crio.core.RoleAddress;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.message.ObjectMessage;
import org.janusproject.kernel.message.StringMessage;
import org.janusproject.kernel.organization.holonic.message.RequestCapacityMessage;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.util.random.RandomNumber;

/**
 * Head role inside an holonic organization.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Head extends Role {

	/**
	 * The map associating the list of capacites that members have offer to the
	 * super-agent The intersection between the various <code>commitments</code>
	 * of each <code>Part</code>.
	 */
	private final Map<Class<? extends Capacity>, List<AgentAddress>> availableCapacities;

	private int nbPartMember;

	private int partDone = 0;

	private boolean allreferenced = false;

	private Message m;

	private int current = 1; // the current state

	/**
	 * Create the Head role.
	 */
	public Head() {
		super();
		this.availableCapacities = new HashMap<Class<? extends Capacity>, List<AgentAddress>>();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public final Status live() {
		this.current = Run();
		return StatusFactory.ok(this);
	}

	/**
	 * Run the behaviour. 
	 */
	@SuppressWarnings("unchecked")
	private int Run() {
		// TODO: write a behaviour subfunction for each task of the head role. See below for the tasks.
		// Mission 1 : build a list of the members' capacities
		// Mission 2 : manage the calls from the super agent to emergent capacities
		switch (this.current) {
		/** **************************************************************************** */
		/** Member capacities referencement * */
		case 1:
			this.nbPartMember = getPlayers(Part.class, getGroupAddress()).totalSize();
			this.partDone = 0;
			return 2;

		case 2:
			this.m = getMailbox().removeFirst();
			if ((this.m != null) && (this.m instanceof ObjectMessage)
					&& (!this.allreferenced)) {
				return 3;
			}
			if ((this.m != null) && (this.m instanceof RequestCapacityMessage)
					&& (this.allreferenced)) {
				return 4;
			}
			return 1;

		case 3:
			++this.partDone;
			debug("Got a message of referencement"); //$NON-NLS-1$
			RoleAddress adr = (RoleAddress)this.m.getSender();
			referenceCapacities(
					(List<Class<? extends Capacity>>) ((ObjectMessage) this.m)
							.getContent(), adr.getPlayer());
			if (this.partDone == this.nbPartMember) {
				debug(
						"Head : All part are referenced");//$NON-NLS-1$
				this.allreferenced = true;
				return 2;
			}
			return 1;
			/** **************************************************************************** */
		case 4: // reroute the message to the adapted Part, ramdomly chooseen
				// over the various part owning the requested capacity.
			debug(
					"Got a message of RequestCapacity"); //$NON-NLS-1$
			adr = (RoleAddress)this.m.getSender();
			sendMessage(adr.getRole(), adr.getPlayer(),
					new StringMessage("OK"));// ACK to Super //$NON-NLS-1$
			List<AgentAddress> candidates = this.availableCapacities
			.get(((RequestCapacityMessage<?>) this.m)
					.getRequestedCapacity());
			sendMessage(Part.class,
					candidates.get(RandomNumber
							.nextInt(candidates.size())),
							this.m);
			return 0;

		default:
			return 0;
		}
	}

	/**
	 * Add the specified capacity to the <code>availableCapacities</code>
	 * HashMap of this head
	 * 
	 * @param capacities -
	 *            the set of capacities to reference
	 * @param ha -
	 *            the address of the agent which the capacities have to be
	 *            referenced by this head
	 */
	private void referenceCapacities(
			List<Class<? extends Capacity>> capacities, AgentAddress ha) {
		List<AgentAddress> member;
		for (Class<? extends Capacity> c : capacities) {
			member = this.availableCapacities.get(c);
			if (member != null) {
				member.add(ha);
			} else {
				member = new LinkedList<AgentAddress>();
				member.add(ha);
				this.availableCapacities.put(c, member);
			}
		}
	}

}
