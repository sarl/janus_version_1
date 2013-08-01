/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2008-2012 Janus Core Developers
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

import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This logger does nothing.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BlackHoleLogger
extends Logger {

	/** Singleton.
	 */
	public static final BlackHoleLogger SINGLETON = new BlackHoleLogger();
	
	/**
	 */
	private BlackHoleLogger() {
		super(null, null);
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceBundle getResourceBundle() {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceBundleName() {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setFilter(Filter newFilter) throws SecurityException {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Filter getFilter() {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(LogRecord record) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String msg, Object param1) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String msg, Object params[]) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(Level level, String msg, Throwable thrown) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logp(Level level, String sourceClass, String sourceMethod,
						String msg, Object param1) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logp(Level level, String sourceClass, String sourceMethod,
						String msg, Object params[]) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logp(Level level, String sourceClass, String sourceMethod,
							String msg, Throwable thrown) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod, 
				String bundleName, String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod,
				String bundleName, String msg, Object param1) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod,
				String bundleName, String msg, Object params[]) {
    	//  	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logrb(Level level, String sourceClass, String sourceMethod,
					String bundleName, String msg, Throwable thrown) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void entering(String sourceClass, String sourceMethod) {
    	//
    }

    /**
     */
    @Override
    public void entering(String sourceClass, String sourceMethod, Object param1) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void entering(String sourceClass, String sourceMethod, Object params[]) {
    	//
    }

    /**
     */
    @Override
    public void exiting(String sourceClass, String sourceMethod) {
    	//
    }


    /**
     */
    @Override
    public void exiting(String sourceClass, String sourceMethod, Object result) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void severe(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warning(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void config(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fine(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finer(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finest(String msg) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(Level newLevel) throws SecurityException {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Level getLevel() {
    	return Level.OFF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoggable(Level level) {
    	return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addHandler(Handler handler) throws SecurityException {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeHandler(Handler handler) throws SecurityException {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Handler[] getHandlers() {
    	return new Handler[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setUseParentHandlers(boolean useParentHandlers) {
    	//
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean getUseParentHandlers() {
    	return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getParent() {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(Logger parent) {
    	//
    }

}
