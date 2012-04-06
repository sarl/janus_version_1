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
package org.janusproject.kernel.util.prototype;

/**
 * Exception when an object invocation does not
 * respect a predefined prototype.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PrototypeException extends AssertionError {

	private static final long serialVersionUID = 1345678823456785342L;

	private final Class<?> annotatedType;
	private final Object[] parameters;
	private final PrototypeScope scope; 
	
	/**
	 * @param scope is the validation stage which causes this exception.
	 * @param annotatedType is the failed object
	 * @param parameters are the values which are cause the failure
	 * @param message is the explanation of the failure.
	 */
	public PrototypeException(
			PrototypeScope scope,
			Class<?> annotatedType,
			Object[] parameters,
			String message) {
		super(message);
		this.scope = scope;
		this.annotatedType = annotatedType;
		this.parameters = parameters;
	}
	
	/** Replies the scope of the exception.
	 * 
	 * @return the scope of the exception.
	 */
	public PrototypeScope getScope() {
		return this.scope;
	}
	
	/** Replies the capacity which has failed.
	 * 
	 * @return the failed capacity.
	 */
	public Class<?> getAnnotatedObject() {
		return this.annotatedType;
	}

	/** Replies the values which cause the failure.
	 * 
	 * @return the value passed to capacity implementation.
	 */
	public Object[] getParameters() {
		return this.parameters;
	}
	
}
