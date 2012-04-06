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
package org.janusproject.kernel;

import java.util.EventObject;

import org.janusproject.kernel.address.AgentAddress;

/**
 * Kernel event.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelEvent extends EventObject {
	
	private static final long serialVersionUID = -2467863932221781286L;
	
	private final boolean isKernel;
	private final AgentAddress agent;
	private final KernelEventType type;
	
	/**
	 * @param type is the type of event.
	 * @param kernel is the kernel which has fired this event.
	 * @param agent is the address of the agent concerned by this event.
	 * @param isKernel indicates if the <var>agent</var> is a kernel agent or not.
	 */
	public KernelEvent(KernelEventType type, Kernel kernel, AgentAddress agent, boolean isKernel) {
		super(kernel);
		this.type = type;
		this.agent = agent;
		this.isKernel = isKernel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Kernel getSource() {
		return (Kernel)super.getSource();
	}
	
	/** Replies the type of this event.
	 * 
	 * @return the type of this event.
	 */
	public KernelEventType getType() {
		return this.type;
	}
	
	/** Replies the agent concerned by this event.
	 * 
	 * @return the address of the agent.
	 */
	public AgentAddress getAgent() {
		return this.agent;
	}

	/** Indicates if the agent concerned by
	 * this event is a kernel agent or not.
	 * 
	 * @return <code>true</code> if the agent
	 * is a kernel agent, otherwise <code>false</code>
	 */
	public boolean isKernelAgent() {
		return this.isKernel;
	}

	/**
	 * Type of kernel event.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum KernelEventType  {

		/** The agent was launched.
		 */
		AGENT_LAUNCHING,
		
		/** The agent was killed.
		 */
		AGENT_KILLING;

	}
	
}
