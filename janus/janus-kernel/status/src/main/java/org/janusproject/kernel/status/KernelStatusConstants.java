/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.status;

/**
 * Status codes used by the Janus kernel.
 *
 * @author $Author: srodriguez$
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class KernelStatusConstants {
	
	/**
	 * Error code used when a role is not defined in the parent organization of a group 
	 */
	public static final int ROLE_NOT_DEFINED_IN_ORGANIZATION = 1000;

	/**
	 * Error code used when a messages requests a specific role player that
	 * is no longer local.
	 * Probable behavior for this type of behavior is that the kernels are out of synch.
	 * 
	 */
	public static final int NO_SUCH_ROLEPLAYER = 1001;
	
	/**
	 * Error code used when no local player are found for a given role
	 */
	public static final int NO_ROLEPLAYER = 1002;

	/**
	 * Remote kernel is not supported. 
	 */
	public static final int REMOTE_KERNEL_NOT_SUPPORTED = 1003;

	/**
	 * Error code used when a heavy agent encountered an exception. 
	 */
	public static final int HEAVY_AGENT_FAILED = 1004;

	/**
	 * No kernel agent found 
	 */
	public static final int NO_KERNEL_AGENT = 1005;

	/**
	 * A agent was not from according to a given address. 
	 */
	public static final int INVALID_ADDRESS_AGENT_NOT_FOUND = 1006;

	/**
	 * Agent is already dead. 
	 */
	public static final int AGENT_IS_DEAD = 1007;
	
	/** The agent which want to kill another agent has not
	 * the rights to kill that agent.
	 */
	public static final int KILL_AGENT_FORBIDDEN = 1008;
	
	/** The state of an agent is not valid for the activation stage.
	 */
	public static final int UNEXPECTED_AGENT_STATE_DURING_ACTIVATION = 1009;

	/** The state of an agent is not valid for the destruction stages.
	 */
	public static final int UNEXPECTED_AGENT_STATE_DURING_DESTRUCTION = 1010;

	/** The real Kernel termination is verified by a delayed look-up
	 * task. This error code indicates that the delayed task was
	 * not accepted for execution by the thread layer from the
	 * Java virtual machine. Consequently if some bug is existing in Kernel code,
	 * which is causing the Kernel to not terminate after a
	 * <code>killMe</code> invocation, then the killed Kernel will never stop
	 * its execution. 
	 */
	public static final int NO_KERNEL_TERMINATION_LOOK_UP_TASK = 1011;

	/**
	 * Cancelation without a specific error code.
	 * Mainly used outside the Janus kernel code.
	 */
	public static final int CANCELATION = -1;

	/**
	 * Error without a specific error code.
	 * Mainly used outside the Janus kernel code.
	 */
	public static final int ERROR = -2;

	/**
	 * Warning without a specific error code.
	 * Mainly used outside the Janus kernel code.
	 */
	public static final int WARNING = -3;

	/**
	 * Success.
	 */
	public static final int SUCCESS = 0;

}
