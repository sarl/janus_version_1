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

import java.util.regex.Pattern;

/** Represents a condition failure. 
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ExpectingValueConditionFailure
implements ConditionFailure {

	private static final long serialVersionUID = 952806234712099698L;
	
	private final String variable;
	private final String current;
	private final String expecting;
	
	/**
	 * @param variable is the name of the variable which has caused the failure.
	 * @param currentValue is the value which has caused the failure
	 * @param expectedValue is the expected value.
	 */
	public ExpectingValueConditionFailure(String variable, String currentValue, String expectedValue) {
		this.variable = variable;
		this.current = currentValue;
		this.expecting = expectedValue;
	}
	
	/** Replies the string representation of the condition failure.
	 * 
	 * @return the string representation of the condition failure.
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.variable);
		buffer.append("=\""); //$NON-NLS-1$
		buffer.append(Pattern.quote(this.current));
		buffer.append("; ("); //$NON-NLS-1$
		buffer.append(this.variable);
		buffer.append("==\""); //$NON-NLS-1$
		buffer.append(Pattern.quote(this.expecting));
		buffer.append("\")"); //$NON-NLS-1$
		return buffer.toString();
	}

}
