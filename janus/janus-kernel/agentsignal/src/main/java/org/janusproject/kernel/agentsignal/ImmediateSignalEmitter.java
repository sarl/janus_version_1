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

import org.janusproject.kernel.util.event.ListenerCollection;

/**
 * An emiter of inner-agent signal with immediately notify
 * listeners when a signal was fired.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated see {@link InstantSignalManager}
 */
@Deprecated
public class ImmediateSignalEmitter
implements SignalEmitter {
	
	private ListenerCollection<SignalListener> listeners = null;
	
	/** {@inheritDoc}
	 */
	@Override
	public void addSignalListener(SignalListener listener) {
		if (this.listeners==null) this.listeners = new ListenerCollection<SignalListener>();
		this.listeners.add(SignalListener.class, listener);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void removeSignalListener(SignalListener listener) {
		if (this.listeners!=null) {
			this.listeners.remove(SignalListener.class, listener);
			if (this.listeners.isEmpty()) {
				this.listeners = null;
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void fireSignal(Signal signal) {
		assert(signal!=null);
		if (this.listeners!=null) {
			Class<? extends Signal> type = signal.getClass();
			for(SignalListener listener : this.listeners.getListeners(SignalListener.class)) {
				if (listener.isSupportedSignalType(type))
					listener.onSignal(signal);
			}
		}
	}

}
