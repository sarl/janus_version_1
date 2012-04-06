/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.janusproject.kernel.bench.organization.message;

import java.util.concurrent.atomic.AtomicInteger;

import org.janusproject.kernel.address.AgentAddress;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.bench.api.BenchUtil;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.crio.core.Organization;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;

/** Iddle agent for benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class EmitterAgent extends Agent {

	private static final long serialVersionUID = 8804935270327139156L;

	private final Class<? extends Organization> organization;
	private final AgentAddress receiver;
	private final Class<? extends Role> role;
	private AtomicInteger flag;
	
	/**
	 * @param flag
	 * @param organization
	 * @param receiver
	 * @param role
	 */
	public EmitterAgent(AtomicInteger flag, Class<? extends Organization> organization, AgentAddress receiver, Class<? extends Role> role) {
		this.organization = organization;
		this.receiver = receiver;
		this.role = role;
		this.flag = flag;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		Status s = super.activate(parameters);
		setMailbox(BenchUtil.createMailboxForSendingBenchs());
		GroupAddress group = getOrCreateGroup(this.organization);
		requestRole(EmitterRole.class, group, this.receiver, this.role);
		return s;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		if (this.flag==null) {
			super.live();
		}
		else {
			this.flag.incrementAndGet();
			this.flag = null;
		}
		return null;
	}

}