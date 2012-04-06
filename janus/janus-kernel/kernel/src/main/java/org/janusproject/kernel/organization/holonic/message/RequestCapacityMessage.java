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
package org.janusproject.kernel.organization.holonic.message;

import java.util.UUID;

import org.janusproject.kernel.crio.capacity.Capacity;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.AbstractContentMessage;

/**
 * This message allow the implementation of non direct atomic implementation by 
 * forwarding the capacity call to the member owning the required capacity
 * This message transit by the head and it is rerouted to the approriate member, 
 * only the head can know which member is able to satisfy this request.
 *  
 * @param <CT> is the type of the requested capacity.
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RequestCapacityMessage<CT extends Capacity>
extends AbstractContentMessage<Class<CT>> {
	
	private static final long serialVersionUID = -8767898009885515378L;

	/**
	 * the capacity that should be executed
	 */
	private final Class<CT> requestedCapacity;

	/**
	 * The role of the super-agent which has originally called the non atomic capacity
	 */
	private final Role caller;

	/**
	 * The user defined string used to unambigously identify a capacity call
	 */
	private final UUID identifier;

	/**
	 * the list of input parameter useful for the capacity computation
	 */
	private final Object[] input;

	/**
	 * Builds a new Request capacity Message
	 * @param capacity - the capacity to call
	 * @param icaller - the reference to the role which has initially call the capacity
	 * @param callIdentifier - the user defined string used to unambigously identify a capacity call
	 * @param inputValues - the list of input parameter useful for the capacity computation
	 */
	public RequestCapacityMessage(Class<CT> capacity,
			Role icaller, UUID callIdentifier, Object... inputValues) {
		this.requestedCapacity = capacity;
		this.caller = icaller;
		this.identifier = null;
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
	 * @return the reference to the role which initially call the capacity
	 */
	public Role getInitialCaller() {
		return this.caller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<CT> getContent() {
		return getRequestedCapacity();
	}
}
