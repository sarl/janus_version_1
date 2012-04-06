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

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;
import org.janusproject.kernel.configuration.JanusProperty;

/**
 * Wrapper from Apache to Sun logger.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class Category implements AppenderAttachable {

	protected final java.util.logging.Logger sunLogger;
	
	protected Category(java.util.logging.Logger sunLogger) {
		if ((Boolean)System.getProperties().get(JanusProperty.JXTA_LOGGING.getPropertyName())) {
			this.sunLogger = sunLogger;
		}
		else { 
			this.sunLogger = null;
		}
	}
	
	public void addAppender(Appender newAppender) {
		throw new UnsupportedOperationException();
	}

	public void assertLog(boolean assertion, String msg) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.FINER, msg);
		}
	}

	public void callAppenders(LoggingEvent event) {
		throw new UnsupportedOperationException();
	}

	public void debug(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.FINE, message.toString());
		}
	}

	public void debug(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.FINE, message.toString(), t);
		}
	}

	public void error(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.SEVERE, message.toString());
		}
	}

	public void error(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.SEVERE, message.toString(), t);
		}
	}

	public static Logger exists(String name) {
		return null;
	}

	public void fatal(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.SEVERE, message.toString());
		}
	}

	public void fatal(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.SEVERE, message.toString(), t);
		}
	}

	public boolean getAdditivity() {
		throw new UnsupportedOperationException();
	}

	public Enumeration<?> getAllAppenders() {
		throw new UnsupportedOperationException();
	}

	public Appender getAppender(String name) {
		throw new UnsupportedOperationException();
	}

	public Level getEffectiveLevel() {
		return Level.OFF;
	}

	public Priority getChainedPriority() {
		throw new UnsupportedOperationException();
	}

	public static Enumeration getCurrentCategories() {
		throw new UnsupportedOperationException();
	}

	public static Category getInstance(String name) {
		throw new UnsupportedOperationException();
	}

	public static Category getInstance(Class clazz) {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public Category getParent() {
		throw new UnsupportedOperationException();
	}

	public Level getLevel() {
		throw new UnsupportedOperationException();
	}

	public Level getPriority() {
		throw new UnsupportedOperationException();
	}


	public static Category getRoot() {
		throw new UnsupportedOperationException();
	}

	public ResourceBundle getResourceBundle() {
		throw new UnsupportedOperationException();
	}

	public void info(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString());
		}
	}

	public void info(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString(), t);
		}
	}

	public boolean isAttached(Appender appender) {
		return false;
	}

	public boolean isDebugEnabled() {
		if (this.sunLogger!=null) {
			return this.sunLogger.isLoggable(java.util.logging.Level.FINE);
		}
		return false;
	}

	public boolean isEnabledFor(Priority level) {
		return false;
	}

	public boolean isInfoEnabled() {
		if (this.sunLogger!=null) {
			return this.sunLogger.isLoggable(java.util.logging.Level.INFO);
		}
		return false;
	}

	public void l7dlog(Priority priority, String key, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.FINE, key, t);
		}
	}

	public void l7dlog(Priority priority, String key,  Object[] params, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.FINE, key, t);
		}
	}

	public void log(Priority priority, Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString(), t);
		}
	}

	public void log(Priority priority, Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString());
		}
	}

	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.INFO, message.toString());
		}
	}

	public void removeAllAppenders() {
		throw new UnsupportedOperationException();
	}

	public void removeAppender(Appender appender) {
		throw new UnsupportedOperationException();
	}

	public void removeAppender(String name) {
		throw new UnsupportedOperationException();
	}

	public void setAdditivity(boolean additive) {
		throw new UnsupportedOperationException();
	}

	public void setLevel(Level level) {
		throw new UnsupportedOperationException();
	}

	public void setPriority(Priority priority) {
		throw new UnsupportedOperationException();
	}

	public void setResourceBundle(ResourceBundle bundle) {
		throw new UnsupportedOperationException();
	}

	public static void shutdown() {
	}

	public void warn(Object message) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.WARNING, message.toString());
		}
	}

	public void warn(Object message, Throwable t) {
		if (this.sunLogger!=null) {
			this.sunLogger.log(java.util.logging.Level.WARNING, message.toString(), t);
		}
	}
	
}