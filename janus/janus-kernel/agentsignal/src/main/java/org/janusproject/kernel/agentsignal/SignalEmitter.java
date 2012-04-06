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
package org.janusproject.kernel.agentsignal;

/**
 * An emiter of inner-agent signal.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link SignalManager}
 */
@Deprecated
public interface SignalEmitter {
	
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

}
