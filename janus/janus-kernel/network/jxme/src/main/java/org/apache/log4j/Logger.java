/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
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
package org.apache.log4j;

import java.util.logging.Level;

/**
 * Wrapper from Apache to Sun logger.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class Logger extends Category {

	static public Logger getLogger(String name) {
		return new Logger(java.util.logging.Logger.getLogger(name));
	}

	static public Logger getLogger(Class clazz) {
		return new Logger(java.util.logging.Logger.getLogger(clazz.getCanonicalName()));
	}

	public static Logger getRootLogger() {
		return new Logger(java.util.logging.Logger.getAnonymousLogger());
	}

	private Logger(java.util.logging.Logger sunLogger) {
		super(sunLogger);
	}
	
	public void trace(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString());
		}
	}

	public void trace(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString(), t);
		}
	}

	public boolean isTraceEnabled() {
		if (this.sunLogger!=null) {
			this.sunLogger.isLoggable(Level.INFO);
		}
		return false;
	}

}
