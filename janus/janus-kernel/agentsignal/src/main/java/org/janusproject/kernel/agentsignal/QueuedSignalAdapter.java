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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.janusproject.kernel.util.autoremove.AutoremoveIterator;

/**
 * A listener on signals which is enqueuing signals.
 * <p>
 * Note that the function {@link #iterator()}
 * replies an iterator that is consuming the signals
 * from the queue.
 * 
 * @param <T> is the type of the expected signal.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class QueuedSignalAdapter<T extends Signal>
implements SignalListener, Iterable<T> {

	/** Reference to the last received signal.
	 */
	private final Queue<T> signals = new ConcurrentLinkedQueue<T>();

	private final Class<T> type;

	/**
	 * @param type is the type of the epxected signal.
	 */
	public QueuedSignalAdapter(Class<T> type) {
		assert(type!=null);
		this.type = type;
	}

	/** Replies the first available received signal in the queue.
	 * The replied message is removed from the queue of signals.
	 * It means that a second call to <code>getFirstAvailableSignal</code>
	 * does not return the same signal.
	 * 
	 * @return the first available signal, or <code>null</code> if none.
	 * @since 0.4
	 */
	public T getFirstAvailableSignal() {
		if (this.signals.isEmpty()) return null;
		return this.signals.remove();
	}

	/** Replies the number of enqueued signals.
	 * 
	 * @return the number of enqueued signals.
	 * @since 0.4
	 */
	public int getQueueSize() {
		return this.signals.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSignal(Signal signal) {
		if (signal!=null && this.type.isInstance(signal)) {
			this.signals.add(this.type.cast(signal));
		}
	}

	/** Clear any reference to the last received signal.
	 */
	public void clear() {
		this.signals.clear();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note that this function
	 * replies an iterator that is consuming the signals
	 * from the queue.
	 */
	@Override
	public Iterator<T> iterator() {
		return new AutoremoveIterator<T>(this.signals.iterator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.signals.toString();
	}

}
