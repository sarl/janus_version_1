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

/** Represents a generic condition which is always <code>true</code>. 
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class TrueCondition implements Condition<Object> {

	private static final long serialVersionUID = -8421012955685916980L;

	/**
	 * {@inheritDoc}
	 * @return <tt>true</tt>
	 */
	@Override
	public boolean evaluate(Object object) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @return <tt>null</tt>
	 */
	@Override
	public ConditionFailure evaluateFailure(Object object) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @return {@code "true"}
	 */
	@Override
	public String toString() {
		return Boolean.TRUE.toString();
	}

}
