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
 * A listener on signals which is remembering only the last signal.
 * 
 * @param <T> is the type of the expected signal.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LastSignalAdapter<T extends Signal> implements SignalListener {

	/** Reference to the last received signal.
	 */
	private T lastSignal = null;
	
	private final Class<T> type;
	
	/**
	 * @param type is the type of the epxected signal.
	 */
	public LastSignalAdapter(Class<T> type) {
		assert(type!=null);
		this.type = type;
	}
	
	/** Replies the last received signal.
	 * 
	 * @return the last received signal, or <code>null</code> if none.
	 */
	public T getLastReceivedSignal() {
		return this.lastSignal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSignal(Signal signal) {
		if (signal!=null && this.type.isInstance(signal)) {
			this.lastSignal = this.type.cast(signal);
		}
	}
	
	/** Clear any reference to the last received signal.
	 */
	public void clear() {
		this.lastSignal = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("["); //$NON-NLS-1$
		if (this.lastSignal!=null)
			s.append(this.lastSignal);
		s.append("]"); //$NON-NLS-1$
		return s.toString();
	}

}
