/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2011 Janus Core Developers
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
package org.janusproject.kernel.util.selector;


/**
 * This class selects objects according to their types.
 * <p>
 * Selected objects are whose with the embedded type in selector.
 * 
 * @param <M> is the type of object supported by the selector.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TypeSelector<M>
implements Selector<M> {

	private final Class<M> type;
	
	/**
	 * @param type is the type of the mails to select.
	 */
	public TypeSelector(Class<M> type) {
		assert(type!=null);
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelected(Object msg) {
		return msg!=null && this.type.isInstance(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<M> getSupportedClass() {
		return this.type;
	}

}
