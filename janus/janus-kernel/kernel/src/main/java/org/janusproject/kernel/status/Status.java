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

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * A status object represents the outcome of an operation.
 * <p>
 * All main janus operations or exception returns or carry a status object
 * to indicate the outcome of the operation (details of failures)
 * or what went wrong in the exception 
 * <p>
 * A status carries the following information:
 * <ul>
 * <li> a string identifier (required)</li>
 * <li> a severity (required)</li>
 * <li> status code (required)</li>
 * <li> message (required) - localized to current locale</li>
 * <li> exception (optional) - for problems stemming from a failure at
 *    a lower level</li>
 * </ul>
 * Some status objects, known as multi-statuses, have other status objects 
 * as children.
 * </p>
 * 
 * <p>
 * The class <code>SingleStatus</code> is the standard public implementation
 * of status objects; the subclass <code>MultipleStatus</code> is the
 * implements multi-status objects.
 * </p>
 * 
 * This class was largely inspired by the notions of IStatus/Status/MultiStatus
 * of org.eclipse.core.runtime package
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Status extends Serializable {

	/**
	 * Returns the severity. The severities are as follows (in
	 * descending order):
	 * <ul>
	 * <li><code>FATAL</code> - a serious error (extremly severe))</li>
	 * <li><code>CANCEL</code> - cancelation occurred</li>
	 * <li><code>ERROR</code> - an error (most severe)</li>
	 * <li><code>FAILURE</code> - a failure </li>
	 * <li><code>WARNING</code> - a warning (least severe)</li>
	 * <li><code>INFO</code> - an informational message</li>
	 * <li><code>OK</code> - everything is just fine</li>
	 * </ul>
	 * For a more detailed description, report to the textual descrition 
	 * of severity level in the <code>IStatus.Severity</code> enum.
	 * <p>
	 * The severity of a multi-status is defined to be the maximum
	 * severity of any of its children, or <code>OK</code> if it has
	 * no children.
	 * </p>
	 *
	 * @return the severity: one of <code>IStatus.Severity</code> enum value
	 * 
	 * @see StatusSeverity
	 * @see #matches(StatusSeverity)
	 */
	public StatusSeverity getSeverity();

	/**
	 * Returns the plug-in-specific status code describing the outcome.
	 *
	 * @return plug-in-specific status code
	 */
	public int getCode();

	/**
	 * Returns the relevant low-level exception, or <code>null</code> if none. 
	 * For example, when an operation fails because of a network communications
	 * failure, this might return the <code>java.io.IOException</code>
	 * describing the exact nature of that failure.
	 *
	 * @return the relevant low-level exception, or <code>null</code> if none
	 */
	public Throwable getException();

	/**
	 * Returns the message describing the outcome.
	 * The message is localized to the current locale.
	 *
	 * @return a localized message
	 */
	public String getMessage();

	/**
	 * Returns the unique identifier of the class associated with this status
	 * (this is the plug-in that defines the meaning of the status code).
	 *
	 * @return the unique identifier of the provider class
	 */
	public String getProviderID();

	/**
	 * Returns whether this status indicates everything is okay
	 * (neither cancel, failure, error, nor fatal).
	 *
	 * @return <code>true</code> if this status has severity
	 *    <code>OK</code>, <code>WARNING</code> or <code>INFO</code>,
	 *    and <code>false</code> otherwise
	 */
	public boolean isSuccess();

	/**
	 * Returns whether this status indicates a failure
	 * (cancel, failure, error, or fatal).
	 *
	 * @return <code>true</code> if this status has a failure severity,
	 * and <code>false</code> otherwise
	 */
	public boolean isFailure();

	/**
	 * Returns whether this status may be logged or not.
	 * A status may be logged if its severity is associated
	 * to a printable log level.
	 *
	 * @return <code>true</code> if this status has a failure severity,
	 * and <code>false</code> otherwise
	 * @since 0.5
	 */
	public boolean isLoggable();

	/**
	 * Set if this status may be logged or not.
	 * A status may be logged if its severity is associated
	 * to a printable log level.
	 *
	 * @param loggable is <code>true</code> if this status may be loggable
	 * depending on its severity, <code>false</code> if this status
	 * should never be logged whatever its severity.
	 * @since 0.5
	 */
	public void setLoggable(boolean loggable);

	/**
	 * Returns the string representation of this status,
	 * this method is essentially used by the logging system.
	 * @return the string representation of this status
	 */
	@Override
	public String toString();
	
	/** Log this status on the given logger.
	 * 
	 * @param logger
	 * @since 0.5
	 */
	public void logOn(Logger logger);

	/**
	 * Returns whether the severity of this status matches the given
	 * mask of the specified severity.
	 *
	 * @param severity - the Severity to match
	 * @return <code>true</code> if there is at least one match, 
	 *    <code>false</code> if there are no matches
	 * @see StatusSeverity
	 * @see #getSeverity()
	 */
	public boolean matches(StatusSeverity severity);
	
	/**
	 * Returns whether the severity of this status matches the given
	 * mask of the specified severity.
	 *
	 * @param severity - the Severity to match
	 * @return <code>true</code> if there is at least one match, 
	 *    <code>false</code> if there are no matches
	 * @see StatusSeverity
	 * @see #getSeverity()
	 */
	public boolean matches(int severity);
	
	/** Pack this status to obtain the more simple status.
	 *
	 * @param provider is the provider to use if no provider is given by this status.
	 * @return this status or a more simple status.
	 */
	public Status pack(String provider);

	/** Pack this status to obtain the more simple status.
	 *
	 * @param provider is the provider to use if no provider is given by this status.
	 * @return this status or a more simple status.
	 */
	public Status pack(Class<?> provider);

	/** Pack this status to obtain the more simple status.
	 *
	 * @param provider is the provider to use if no provider is given by this status.
	 * @return this status or a more simple status.
	 */
	public Status pack(Object provider);

}
