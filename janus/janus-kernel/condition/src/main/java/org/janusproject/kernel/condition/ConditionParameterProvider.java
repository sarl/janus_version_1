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
package org.janusproject.kernel.condition;

import java.util.Iterator;

/**
 * This interface represents an object that contains parameters which
 * could be evaluated by a {@link Condition}.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @deprecated no replacement
 */
@Deprecated
public interface ConditionParameterProvider {
	
	/** Replies the count of parameters.
	 * 
	 * @return the count of parameters.
	 */
	public int getConditionParameterCount();
	
	/** Replies the parameter at the given index.
	 * 
	 * @param index
	 * @return the parameter value or <code>null</code>.
	 */
	public Object getConditionParameterAt(int index);

	/** Replies the parameter at the given index.
	 * 
	 * @param parameterName
	 * @return the parameter value or <code>null</code>.
	 */
	public Object getConditionParameter(String parameterName);

	/** Replies an iterator on condition parameters.
	 *
	 * @param index
	 * @return the parameter name or <code>null</code>.
	 */
	public String getConditionParameterNameAt(int index);

	/** Replies an iterator on condition parameters.
	 * 
	 * @return the parameters.
	 */
	public Iterator<Object> getConditionParameters();

}
