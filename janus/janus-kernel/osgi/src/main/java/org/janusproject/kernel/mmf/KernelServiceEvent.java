/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2009-2011 Janus Core Developers
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
package org.janusproject.kernel.mmf;

import java.util.EventObject;

import org.janusproject.kernel.mmf.impl.OSGiKernelService;

/**
 * An Event of the {@link OSGiKernelService}.
 * 
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class KernelServiceEvent
extends EventObject {

	private static final long serialVersionUID = 5670119334125303556L;

	/**
	 * Types of event for the {@link OSGiKernelService}.
	 * 
	 * @author $Author: srodriguez$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static enum KernelServiceEventType {
		/**The operation was requested but not yet authorized. At this stage the {@link KernelAuthority} 
		 * was not called yet.
		 */
		OPERATION_REQUESTED,
		/**
		 * {@link KernelAuthority}} has authorized the execution the operation. But the operation itself has
		 * not been executed.
		 */
		OPERATION_APPROVED,
		/**
		 * The operation was executed.
		 */
		OPERATION_EXECUTED;
	}
	
	private final KernelServiceEventType type;
	private final KernelOperation operation;
	
	/**
	 * @return the operation
	 */
	public KernelOperation getOperation() {
		return this.operation;
	}

	/**
	 * @param source is the source of event.
	 * @param type is the type of event
	 * @param operation is the associated operation.
	 */
	public KernelServiceEvent(OSGiKernelService source, KernelServiceEventType type, KernelOperation operation){
		super(source);
		this.type = type;
		this.operation = operation;
	}
	
	/**
	 * Replies the type of event.
	 * 
	 * @return the type of event.
	 */
	public KernelServiceEventType getType(){
		return this.type;
	}
	
}
