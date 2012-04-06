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
package org.janusproject.kernel.organization.holonic.influence;

import java.util.UUID;

import org.janusproject.kernel.agentsignal.Signal;
import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.core.Role;

/**
 * Influence invoked when a capacity is requested.
 * 
 * @param <CT> is the type of the requested capacity.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RequestCapacityInfluence<CT extends Capacity> 
extends Signal {
	
	private static final long serialVersionUID = -3365357039627379692L;

	/**
	 * The capacity that should be executed
	 */
	private Class<CT> requestedCapacity;

	/**
	 * The role of the super-agent which has originally called the non atomic capacity
	 */
	private Role caller;

	/**
	 * The user defined string used to unambigously identify a capacity call
	 */
	private UUID identifier;

	/**
	 * The list of input parameter useful for the capacity computation
	 */
	private Object[] input;

	/**
	 * Builds a new Request capacity Influence
	 * @param capacity - the capacity to call
	 * @param icaller - the reference to the role which has initially call the capacity
	 * @param callIdentifier - the user defined string used to unambigously identify a capacity call
	 * @param inputValues - the list of input parameter useful for the capacity computation
	 */
	public RequestCapacityInfluence(Class<CT> capacity,
			Role icaller, UUID callIdentifier, Object... inputValues) {
		super(icaller);
		this.requestedCapacity = capacity;
		this.caller = icaller;
		this.identifier = callIdentifier;
		this.input = inputValues;
	}

	/**
	 * @return the user-defined string used to identify the capacity call
	 */
	public UUID getCallIdentifier() {
		return this.identifier;
	}

	/**
	 * @return the list of input parameter useful for the capacity computation
	 */
	public Object[] getInputValues() {
		return this.input;
	}

	/**
	 * @return the capacity that should be executed
	 */
	public Class<CT> getRequestedCapacity() {
		return this.requestedCapacity;
	}

	/** 
	 * @return the role of the super-agent which has originally called the non atomic capacity
	 */
	public Role getRoleInfluenceSource() {
		return this.caller;
	}
	
}
