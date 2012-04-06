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

/** Represents an abstract condition. 
 * 
 * @param <O> is the type of parameter provider
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractCondition<O extends ConditionParameterProvider> implements Condition<O> {

	private static final long serialVersionUID = 820007805427160941L;
	
	private final int parametersCount;

	/**
	 * @param parameterCount is the count of parameters required to evaluate this condition.
	 */
	public AbstractCondition(int parameterCount) {
		this.parametersCount = parameterCount;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final int getConditionParameterCount() {
		return this.parametersCount;
	}

}
