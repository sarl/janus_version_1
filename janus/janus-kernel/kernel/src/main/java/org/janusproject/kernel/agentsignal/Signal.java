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

import java.util.EventObject;

/**
 * A type of event.
 * <p>
 * Basically a signal is used to notify about any change and
 * when no agent memory is applied.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class Signal extends EventObject {

	/** Name for anonymous signals.
	 */
	public static final String ANONYMOUS = null;
	
	/**
	 */
	private static final long serialVersionUID = -1212464722733919110L;
	
	private final String name;
	private Object[] values;

	/**
	 * @param source is the source of the signal.
	 */
	public Signal(Object source) {
		this(source, ANONYMOUS);
	}

	/**
	 * @param source is the source of the signal.
	 * @param name is the name of the signal.
	 */
	public Signal(Object source, String name) {
		super(source);
		this.name = name;
		this.values = null;
	}
	
	/**
	 * @param source is the source of the signal.
	 * @param name is the name of the signal.
	 * @param values are the values propagated by this signal.
	 */
	public Signal(Object source, String name, Object... values) {
		super(source);
		this.name = name;
		this.values = values;
	}

	/** Replies the name of the signal.
	 * 
	 * @return the name of the signal.
	 */
	public String getName() {
		return this.name;
	}
	
	/** Replies the values in this signal.
	 * 
	 * @return the values.
	 */
	public Object[] getValues() {
		if (this.values==null) return new Object[0];
		return this.values;
	}

	/** Replies the value at the given position in this signal.
	 * 
	 * @param <T> type is the type of the expected value.
	 * @param index is the position of the value.
	 * @param type is the type of the expected value.
	 * @return the value, or <code>null</code> if the index is invalid
	 * or the type does not corresponds to the value.
	 * @since 0.5
	 */
	public <T> T getValueAt(int index, Class<T> type) {
		if (this.values!=null
			&& index>=0
			&& index<this.values.length) {
			Object v = this.values[index];
			if (v==null || type.isInstance(v)) {
				return type.cast(v);
			}
		}
		return null;
	}

	/** Replies the value at the given position in this signal.
	 * 
	 * @param index is the position of the value.
	 * @return the value, or <code>null</code> if the index is invalid.
	 * @since 0.5
	 */
	public Object getValueAt(int index) {
		if (this.values!=null
			&& index>=0
			&& index<this.values.length) {
			return this.values[index];
		}
		return null;
	}
	
	/** Replies the number of values stored in this signal.
	 * 
	 * @return the number of values stored in this signal.
	 * @since 0.5
	 */
	public int getValueCount() {
		return this.values==null ? 0 : this.values.length;
	}

}
