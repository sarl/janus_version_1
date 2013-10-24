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

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;

/**
 * This enum describes the various status severity level 
 * and their corresponding definition.
 * 
 * Each severity level is associated to a mask enabling 
 * a quick matching, and a textual description. 
 * Except for the <code>Severity.OK</code> that will never match (no mask but a simple zero value)
 *
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum StatusSeverity {
	/**
	 * The nominal case, no problem during operation execution.
	 */
	OK (0x01,Locale.getString(StatusSeverity.class, "OK")), //$NON-NLS-1$
	/**
	 * Informational only
	 */
	INFO (0x02,Locale.getString(StatusSeverity.class, "INFO")), //$NON-NLS-1$
	/**
	 * Warning, there was a problem
	 */
	WARNING (0x04,Locale.getString(StatusSeverity.class, "WARNING")), //$NON-NLS-1$
	/**
	 * The operation has failed, because its inputs doesn't validate requirements for a successful execution
	 */
	FAILURE (0x08,Locale.getString(StatusSeverity.class, "FAILURE")),//$NON-NLS-1$
	/**
	 * An error has broken the operation execution, but the application is still running
	 */
	ERROR (0x10,Locale.getString(StatusSeverity.class, "ERROR")), //$NON-NLS-1$
	/**
	 * The operation execution was interrupted by a cancelation
	 */
	CANCEL (0x20,Locale.getString(StatusSeverity.class, "CANCEL")),//$NON-NLS-1$
	/**
	 * An error has broken the operation execution, and has caused the break down of the whole application
	 */
	FATAL (0x40,Locale.getString(StatusSeverity.class, "FATAL")); //$NON-NLS-1$ 

	StatusSeverity(int imask, String sdescription) {
		this.mask = imask;
		this.description = sdescription;
	}

	private int mask;
	private String description;

	/**
	 * Returns the associated string code identifier of the severity level
	 * @return the associated string code identifier of the severity level
	 */
	public int getMask() {
		return this.mask;
	}

	/**
	 * Explicit by a textual description what does mean a specific severity level
	 * @return a textual description expliciting what does mean a specific severity level
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns whether this status indicates everything is okay
	 * (neither cancel, failure, error, nor fatal).
	 *
	 * @return <code>true</code> if this status has severity
	 *    <code>OK</code>, <code>WARNING</code> or <code>INFO</code>,
	 *    and <code>false</code> otherwise
	 */
	public boolean isSuccess() {
		return this==OK || this==WARNING || this==INFO;
	}

	/**
	 * Returns whether this status indicates a failure
	 * (cancel, failure, error, or fatal).
	 *
	 * @return <code>true</code> if this status has a failure severity,
	 * and <code>false</code> otherwise
	 */
	public boolean isFailure() {
		return this==CANCEL || this==ERROR || this==FAILURE || this==FATAL;
	}
	
	/** Replies the preferred log level for this status severity.
	 * 
	 * @return the preferred log level for this status severity.
	 * @since 0.5
	 */
	public Level toLevel() {
		switch(this) {
		case CANCEL:
		case ERROR:
		case FAILURE:
		case FATAL:
			return Level.SEVERE;
		case WARNING:
			return Level.WARNING;
		case INFO:
			return Level.INFO;
		case OK:
		default:
		}
		return Level.OFF;
	}

}
