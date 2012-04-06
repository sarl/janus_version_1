/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.status;

/**
 * A concrete status implementation based on catched exception.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ExceptionStatus extends SingleStatus {
    
	private static final long serialVersionUID = 8968812086675745731L;

	/**
	 * Creates a new status object.  
	 * The created status has no children.
	 *
	 * @param exception is exception a low-level exception, or <code>null</code> if not applicable
	 */
	public ExceptionStatus(Throwable exception) {
		super();
		assert(exception!=null);
		setSeverity(StatusSeverity.ERROR);
		setCode(KernelStatusConstants.ERROR);

		Throwable cause = exception;
		while (cause.getCause()!=null && cause!=exception) {
			cause = cause.getCause();
		}

		assert(cause!=null);
		setException(cause);
		setMessage(cause.getLocalizedMessage());
		
		StackTraceElement[] stack = exception.getStackTrace();
		if (stack.length>0) {
			setProviderId(stack[0].getClassName());
		}
		else {
			setProviderId(Integer.toString(System.identityHashCode(exception)));
		}
	}
    
}
