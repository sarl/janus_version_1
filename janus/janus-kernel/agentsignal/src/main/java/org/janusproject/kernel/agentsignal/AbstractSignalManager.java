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

import java.util.ArrayList;
import java.util.List;

import org.janusproject.kernel.configuration.JanusProperties;
import org.janusproject.kernel.configuration.JanusProperty;

/**
 * Abstract implementation of a signal manager.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see InstantSignalManager
 * @see BufferedSignalManager
 * @since 0.5
 */
public abstract class AbstractSignalManager implements SignalManager, SignalListener {

	private SignalPolicy policy;
	private SignalManager parentManager;

	/**
	 * Signal listeners.
	 */
	protected List<SignalListener> listeners = null;

	/**
	 * Enqueued Signals.
	 */
	protected QueuedSignalAdapter<Signal> events = null;

	/**
	 * @param properties
	 */
	public AbstractSignalManager(JanusProperties properties) {
		this(properties, null);
	}

	/**
	 * @param properties
	 * @param parent is the signal manager that owns this submanager. All
	 * the signals will be fired by the parent manager.
	 */
	public AbstractSignalManager(JanusProperties properties, SignalManager parent) {
		this.parentManager = parent;
		SignalPolicy pol = null;
		if (this.parentManager!=null) {
			pol = this.parentManager.getPolicy();
		}
		if (pol==null && properties!=null) {
			String v = properties.getProperty(JanusProperty.JANUS_AGENT_SIGNAL_POLICY);
			if (v!=null) {
				try {
					pol = SignalPolicy.valueOf(v);
				}
				catch(Throwable _) {
					//
				}
			}
			
		}
		this.policy = pol==null ? SignalPolicy.FIRE_SIGNAL : pol;
		if (this.parentManager!=null) {
			this.parentManager.addSignalListener(this);
		}
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public SignalManager getParent() {
		return this.parentManager;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Signal getSignal() {
		switch(this.policy) {
		case IGNORE_ALL:
		case FIRE_SIGNAL:
			break;
		case STORE_IN_QUEUE:
			if (this.events!=null) {
				return this.events.getFirstAvailableSignal();
			}
			break;
		default:
		}
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean hasSignal() {
		switch(this.policy) {
		case IGNORE_ALL:
		case FIRE_SIGNAL:
			break;
		case STORE_IN_QUEUE:
			if (this.events!=null) {
				return this.events.getQueueSize()>0;
			}
			break;
		default:
		}
		return false;
	}

	/** Reset this manager: its parent, its events, and its listeners.
	 */
	public void reset() {
		if (this.listeners != null) {
			this.listeners.clear();
			this.listeners = null;
		}
		if (this.events != null) {
			this.events.clear();
			this.events = null;
		}
		if (this.parentManager!=null)
			this.parentManager.removeSignalListener(this);
		this.parentManager = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSignalListener(SignalListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<SignalListener>();
		}
		this.listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeSignalListener(SignalListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
			if (this.listeners.isEmpty()) {
				this.listeners = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fireSignal(Signal signal) {
		if (this.parentManager!=null) {
			this.parentManager.fireSignal(signal);
		}
		else {
			onSignal(signal);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SignalPolicy getPolicy() {
		return this.policy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPolicy(SignalPolicy policy) {
		this.policy = policy;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.events==null ? "[]" : this.events.toString(); //$NON-NLS-1$
	}

}
