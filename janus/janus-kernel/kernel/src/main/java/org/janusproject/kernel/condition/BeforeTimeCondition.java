/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2011 Janus Core Developers
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

/** This condition is satisfied if the current time is
 * strictly before a given time. 
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BeforeTimeCondition extends AbstractCondition<TimeConditionParameterProvider> implements TimeCondition, ConditionFailure {
	
	private static final long serialVersionUID = 8764372659452439246L;
	
	private final float time;
	
	/**
	 * @param limitTime is the time before which the condition is satified.
	 */
	public BeforeTimeCondition(float limitTime) {
		super();
		this.time = limitTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean evaluate(TimeConditionParameterProvider object) {
		return (object.getCurrentTime() < this.time);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConditionFailure evaluateFailure(TimeConditionParameterProvider object) {
		if (object.getCurrentTime() < this.time) return null;
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "<" + this.time; //$NON-NLS-1$
	}
	
}
