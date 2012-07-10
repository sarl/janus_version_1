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

import org.janusproject.kernel.configuration.JanusProperties;

/**
 * Signal manager that is buffering the signal
 * until its function {@link #sync()} is invoked.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see InstantSignalManager
 * @since 0.5
 */
public class BufferedSignalManager extends AbstractSignalManager {

	/**
	 * @param properties
	 */
	public BufferedSignalManager(JanusProperties properties) {
		super(properties);
	}

	/**
	 * @param properties
	 * @param parent is the signal manager that owns this submanager. All
	 * the signals will be fired by the parent manager.
	 */
	public BufferedSignalManager(JanusProperties properties, SignalManager parent) {
		super(properties, parent);
	}
	
	/**
	 * Fire buffered signals in role's listeners.
	 */
	public void sync() {
		switch(getPolicy()) {
		case IGNORE_ALL:
			this.events = null;
			break;
		case FIRE_SIGNAL:
			if (this.events!=null) {
				if (this.listeners != null) {
					for (Signal signal : this.events) {
						for (SignalListener listener : this.listeners) {
							listener.onSignal(signal);
						}
					}
				}
				this.events.clear();
			}
			break;
		case STORE_IN_QUEUE:
			// Do nothing because the events were already stored in the queue.
			break;
		default:
		}
	}

	@Override
	public void onSignal(Signal signal) {
		switch(getPolicy()) {
		case IGNORE_ALL:
			break;
		case FIRE_SIGNAL:
			if (this.events == null) {
				this.events = new QueuedSignalAdapter<Signal>(Signal.class);
			}
			this.events.onSignal(signal);
			break;
		case STORE_IN_QUEUE:
			if (this.events == null) {
				this.events = new QueuedSignalAdapter<Signal>(Signal.class);
			}
			this.events.onSignal(signal);
			
			// notifies the SignalManager children
			if (this.listeners!=null) {
				for (SignalListener listener : this.listeners) {
					if (listener instanceof SignalManager)
						listener.onSignal(signal);
				}
			}
			break;
		default:
		}
	}

}
