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
 * Factory of status.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StatusFactory {

	/** Create a OK status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status ok(String provider) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.OK, 
				provider, 
				KernelStatusConstants.SUCCESS, 
				null, null);
	}

	/** Create a OK status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status ok(Class<?> provider) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.OK, 
				provider.getCanonicalName(), 
				KernelStatusConstants.SUCCESS,
				null, null);
	}

	/** Create a OK status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status ok(Object provider) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.OK, 
				Integer.toString(System.identityHashCode(provider)),
				KernelStatusConstants.SUCCESS,
				null, null);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status cancel(String provider) {
		return cancel(provider, null);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status cancel(Class<?> provider) {
		return cancel(provider, null);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @return the status.
	 */
	public static Status cancel(Object provider) {
		return cancel(provider, null);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 */
	public static Status cancel(String provider, Throwable cause) {
		return cancel(provider, null, cause);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 */
	public static Status cancel(Class<?> provider, Throwable cause) {
		return cancel(provider, null, cause);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 */
	public static Status cancel(Object provider, Throwable cause) {
		return cancel(provider, null, cause);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 * @since 0.5
	 */
	public static Status cancel(String provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.CANCEL, 
				provider, 
				KernelStatusConstants.CANCELATION,
				message, cause);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 * @since 0.5
	 */
	public static Status cancel(Class<?> provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.CANCEL, 
				provider.getCanonicalName(), 
				KernelStatusConstants.CANCELATION,
				message, cause);
	}

	/** Create a CANCEL status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the cancelation.
	 * @return the status.
	 * @since 0.5
	 */
	public static Status cancel(Object provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(
				StatusSeverity.CANCEL, 
				Integer.toString(System.identityHashCode(provider)),
				KernelStatusConstants.CANCELATION, 
				message, cause);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @return the status.
	 */
	public static Status error(String provider, String message) {
		return error(provider, message, null);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @return the status.
	 */
	public static Status error(Class<?> provider, String message) {
		return error(provider, message, null);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @return the status.
	 */
	public static Status error(Object provider, String message) {
		return error(provider, message, null);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status error(String provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.ERROR, 
				provider, 
				KernelStatusConstants.ERROR, message, cause);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status error(Class<?> provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.ERROR, 
				provider.getCanonicalName(), 
				KernelStatusConstants.ERROR, message, cause);
	}

	/** Create a ERROR status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status error(Object provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.ERROR, 
				Integer.toString(System.identityHashCode(provider)), 
				KernelStatusConstants.ERROR, message, cause);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @return the status.
	 */
	public static Status warning(String provider, String message) {
		return error(provider, message, null);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @return the status.
	 */
	public static Status warning(Class<?> provider, String message) {
		return error(provider, message, null);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @return the status.
	 */
	public static Status warning(Object provider, String message) {
		return error(provider, message, null);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status warning(String provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.WARNING, 
				provider, 
				KernelStatusConstants.WARNING, message, cause);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status warning(Class<?> provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.WARNING, 
				provider.getCanonicalName(), 
				KernelStatusConstants.WARNING, message, cause);
	}

	/** Create a WARNING status.
	 * 
	 * @param provider is the provider of the status flag, eg the caller of this function.
	 * @param message is the message which is explaining the warning.
	 * @param cause is the throwable object which is the cause of the error.
	 * @return the status.
	 */
	public static Status warning(Object provider, String message, Throwable cause) {
		assert(provider!=null);
		return new SingleStatus(StatusSeverity.WARNING, 
				Integer.toString(System.identityHashCode(provider)), 
				KernelStatusConstants.WARNING, message, cause);
	}

}
