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
package org.janusproject.kernel.logger;

import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.arakhne.afc.vmutil.OperatingSystem;
import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.address.Address;
import org.janusproject.kernel.time.KernelTimeManager;

/**
 * This class provides several utility functions for Sun's logging system.
 * 
 * @author $Author: sgalland$
 * @author $Author: jeremie.laval@gmail.com$
 * @author $Author: robin.geffroy@gmail.com$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class LoggerUtil {

	private static final AtomicBoolean isLoggingEnabled;
	private static final AtomicBoolean isDefaultConfigurationLoaded = new AtomicBoolean(false);
	private static final AtomicBoolean isShortLogMessage;
	private static Class<? extends Handler> defaultHandlerType = null;
	
	static {
		boolean isEmbeddedOS;
		try {
			isEmbeddedOS = OperatingSystem.ANDROID.isCurrentOS();
		}
		catch(Throwable _) {
			// High probability to be invoked from the inside of an applet
			isEmbeddedOS = true;
		}
		isLoggingEnabled = new AtomicBoolean(!isEmbeddedOS);
		isShortLogMessage = new AtomicBoolean(isEmbeddedOS);
	}
	
	/** Set the type of the preferred handler for the newly loggers.
	 * 
	 * @param type
	 * @since 0.5
	 */
	public static void setHandlerType(Class<? extends Handler> type) {
		synchronized(LoggerUtil.class) {
			defaultHandlerType = type;
		}
	}
	
	/** Replies the type of the preferred handler for the newly loggers.
	 * 
	 * @return the type of the preferred handler for the newly loggers.
	 * @since 0.5
	 */
	public static Class<? extends Handler> getHandlerType() {
		synchronized(LoggerUtil.class) {
			if (defaultHandlerType==null) return ConsoleHandler.class;
			return defaultHandlerType;
		}
	}

	/** Invalidate all invocations to loggers.
	 * When logging is not enabled, every logger replied by this
	 * utility class does nothing.
	 * 
	 * @param isEnable indicates if the logging system is enabled.
	 */
	public static void setLoggingEnable(boolean isEnable) {
		isLoggingEnabled.set(isEnable);
	}

	/** Replies if the logger is authorized to log short messages in place of
	 * the standard messages.
	 * <p>
	 * The content of the short messages depends on the logger.
	 * 
	 * @return <code>true</code> if the short message are enable, otherwise <code>false</code>
	 */
	public static boolean isShortLogMessageEnable() {
		return isShortLogMessage.get();
	}
	
	/** Set if the logger is authorized to log short messages in place of
	 * the standard messages.
	 * <p>
	 * The content of the short messages depends on the logger.
	 * 
	 * @param enable is <code>true</code> to enable, otherwise <code>false</code>
	 */
	public static void setShortLogMessageEnable(boolean enable) {
		isShortLogMessage.set(enable);
	}

	/** Create a logger.
	 * 
	 * @param type is the type of the logger owner.
	 * @param formatter is the formatter to use.
	 * @param loggerId is the identifier of the logger entity.
	 * @return a logger.
	 */
	private static Logger createLogger(Class<?> type, AbstractJanusFormatter formatter) {
		assert(type!=null);
		assert(formatter!=null);
		if (isLoggingEnabled.get()) {
			loadDefaultConfiguration(false);
			StringBuilder className = new StringBuilder(type.getCanonicalName());
			String loggerId = formatter.getFormatterOwner();
			if (loggerId!=null) {
				className.append('$');
				className.append(loggerId);
			}
			Logger logger = Logger.getLogger(className.toString());
			boolean found = false;
			for(Handler h : logger.getHandlers()) {
				h.setFormatter(formatter);
				found = true;
			}
			if (!found) {
				assert(logger.getHandlers().length==0);
				Class<? extends Handler> handlerType = getHandlerType();
				Handler handler;
				try {
					handler = handlerType.newInstance();
				}
				catch (Throwable _) {
					handler = new ConsoleHandler();
				}
				handler.setFormatter(formatter);
				logger.addHandler(handler);
			}
			logger.setUseParentHandlers(false);
			return logger;
		}
		return BlackHoleLogger.SINGLETON;
	}

	/** Create a logger for an role player or an agent.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @param address is the address of the entity
	 * @return a logger.
	 */
	public static Logger createAgentLogger(Class<?> type, KernelTimeManager timeManager, Address address) {
		return createLogger(type, new JanusAgentFormatter(timeManager, address));
	}
	
	/** Create a logger for a kernel.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @param address is the address of the entity
	 * @return a logger.
	 */
	public static Logger createKernelLogger(Class<?> type, KernelTimeManager timeManager, Address address) {
		return createLogger(type, new JanusKernelFormatter(timeManager, address));
	}

	/** Create a logger for a role.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @param address is the address of the entity
	 * @param roleName is the name of the played role.
	 * @return a logger.
	 */
	public static Logger createRoleLogger(Class<?> type, KernelTimeManager timeManager, Address address, String roleName) {
		return createLogger(type, new JanusRoleFormatter(timeManager, address, roleName));
	}

	/** Create a logger for an organization.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @return a logger.
	 */
	public static Logger createOrganizationLogger(Class<?> type, KernelTimeManager timeManager) {
		assert(type!=null);
		return createLogger(type, new JanusOrganizationFormatter(timeManager, type.getName()));
	}

	/** Create a logger for a group.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @param address is the address of the group.
	 * @return a logger.
	 */
	public static Logger createGroupLogger(Class<?> type, KernelTimeManager timeManager, Address address) {
		assert(type!=null);
		return createLogger(type, new JanusGroupFormatter(timeManager, type.getName(), address));
	}

	/** Create a generic logger.
	 * 
	 * @param type is the type of the logger owner.
	 * @param timeManager is the current time manager.
	 * @return a logger.
	 */
	public static Logger createGenericLogger(Class<?> type, KernelTimeManager timeManager) {
		return createLogger(type, new JanusGenericFormatter(timeManager));
	}

	/**
	 * Force all the loggers to use the given log level.
	 * 
	 * @param newLevel is the global level to apply.
	 */
	public static void setGlobalLevel(Level newLevel) {
		if (isLoggingEnabled.get()) {
			loadDefaultConfiguration(false);
			LogManager manager = LogManager.getLogManager();
			if (manager!=null) {
				String name;
				Logger logger;
				Enumeration<String> names = manager.getLoggerNames();
				assert(names!=null);
				while (names.hasMoreElements()) {
					name = names.nextElement();
					logger = manager.getLogger(name);
					if (logger!=null) {
						logger.setLevel(newLevel);
					}
				}
			}
		}
	}

	/**
	 * Load the default configuration for the loggers.
	 * 
	 * @param forceLoading indicates if the default configuration must
	 * be always loaded (if <code>true</code>), or loaded if was never
	 * loaded before (if <code>false</code>).
	 */
	public static void loadDefaultConfiguration(boolean forceLoading) {
		if (isLoggingEnabled.get()) {
			if (forceLoading || !isDefaultConfigurationLoaded.get()) {
				// Force the logging manager to use a predefined
				// configuration.
				// This configuration may be loaded only once time.
				LogManager logManager = LogManager.getLogManager();
				URL defaultConfig = Resources.getResource("/org/janusproject/kernel/logger/defaultLogging.properties"); //$NON-NLS-1$
				if (defaultConfig!=null) {
					try {
						logManager.readConfiguration(defaultConfig.openStream());
						isDefaultConfigurationLoaded.set(true);
					}
					catch(AssertionError ae) {
						throw ae;
					}
					catch(Exception e) {
						Logger.getAnonymousLogger().warning(
								Locale.getString(LoggerUtil.class, "NO_LOGGING_CONFIGURATION")); //$NON-NLS-1$
					}
				}
				else {
					Logger.getAnonymousLogger().warning(
							Locale.getString(LoggerUtil.class, "NO_LOGGING_CONFIGURATION")); //$NON-NLS-1$
				}
			}
		}
	}
	
	/** Force the given logger to have the given output handler.
	 * <p>
	 * All previously added handler will be removed. If any
	 * Janus-dedicated formatter was previously associated to
	 * the logger, this formatter is given to the new handler in
	 * place of its current formatter.
	 * 
	 * @param logger is the logger to set.
	 * @param handler is the new output handler for the logger.
	 */
	public static void setOutputHandler(Logger logger, Handler handler) {
		if (isLoggingEnabled.get()) {
			Formatter f;
			AbstractJanusFormatter formatter = null;
			for(Handler oldHandler : logger.getHandlers()) {
				f = oldHandler.getFormatter();
				if (f instanceof AbstractJanusFormatter) {
					formatter = (AbstractJanusFormatter)f;
				}
				logger.removeHandler(oldHandler);
			}
			if (formatter!=null) {
				handler.setFormatter(formatter);
			}
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);
		}
	}
	
	/** Force the given logger to have the given output handler.
	 * <p>
	 * All previously added handler will be removed. If any
	 * Janus-dedicated formatter was previously associated to
	 * the logger, this formatter is given to the new handler in
	 * place of its current formatter.
	 * 
	 * @param logger is the logger to set.
	 * @param stream is the new output stream for the logger.
	 */
	public static void setOutputStream(Logger logger, OutputStream stream) {
		if (isLoggingEnabled.get()) {
			Formatter f;
			AbstractJanusFormatter formatter = null;
			for(Handler oldHandler : logger.getHandlers()) {
				f = oldHandler.getFormatter();
				if (f instanceof AbstractJanusFormatter) {
					formatter = (AbstractJanusFormatter)f;
				}
				logger.removeHandler(oldHandler);
			}
			Handler handler;
			if (formatter!=null) {
				handler = new StreamHandler(stream, formatter);
			}
			else {
				handler = new StreamHandler(stream, new SimpleFormatter());
			}
			logger.addHandler(handler);
			logger.setUseParentHandlers(false);
		}
	}

	/** Force the given logger to have the given output handler.
	 * <p>
	 * All previously added handler will be removed. If any
	 * Janus-dedicated formatter was previously associated to
	 * the logger, this formatter is given to the new handler in
	 * place of its current formatter.
	 * 
	 * @param logger is the logger to set.
	 * @param channel is the new output channel for the logger.
	 */
	public static void setOutputStream(Logger logger, WritableByteChannel channel) {
		setOutputStream(logger, Channels.newOutputStream(channel));
	}

	/** Force the given logger to output on console.
	 * <p>
	 * All previously added handler will be removed. If any
	 * Janus-dedicated formatter was previously associated to
	 * the logger, this formatter is given to the new handler in
	 * place of its current formatter.
	 * 
	 * @param logger is the logger to set.
	 * @see ConsoleHandler
	 */
	public static void setOutputToConsole(Logger logger) {
		setOutputHandler(logger, new ConsoleHandler());
	}

}
