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
package org.janusproject.kernel.agentsignal;

/**
 * An emiter of inner-agent signal.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public interface SignalManager {
	
	/** Replies the parent of thie signal manager.
	 * When a signal manager has a parent, all 
	 * the signals are forwarded to this parent,
	 * and incoming signals come from the parent.
	 * 
	 * @return the parent of thie signal manager.
	 */
	public SignalManager getParent();

	/** Replies the policy of management of
	 * the signals.
	 * @return the signal management policy.
	 */
	public SignalPolicy getPolicy();
	
	/** Set the policy of management of
	 * the signals.
	 * 
	 * @param policy is the new signal management policy.
	 */
	public void setPolicy(SignalPolicy policy);

	/** Register a signal listener.
	 * 
	 * @param listener
	 */
	public void addSignalListener(SignalListener listener);
	
	/** Unregister a signal listener.
	 * 
	 * @param listener
	 */
	public void removeSignalListener(SignalListener listener);

	/** Fire a signal.
	 * 
	 * @param signal
	 */
	public void fireSignal(Signal signal);

	/** Replies the next available signal is possible.
	 * <p>
	 * A signal is available only if the current
	 * policy is {@link SignalPolicy#STORE_IN_QUEUE}
	 * and if a signal is inside the queue.
	 * 
	 * @return the next available signal or <code>null</code>.
	 */
	public Signal getSignal();

	/** Replies if a signal is available.
	 * <p>
	 * A signal is available only if the current
	 * policy is {@link SignalPolicy#STORE_IN_QUEUE}
	 * and if a signal is inside the queue.
	 * 
	 * @return <code>true</code> if a signal is available,
	 * otherwise <code>false</code>.
	 */
	public boolean hasSignal();

}
