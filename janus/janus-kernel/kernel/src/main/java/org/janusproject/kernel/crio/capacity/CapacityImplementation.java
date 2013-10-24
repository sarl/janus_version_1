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
package org.janusproject.kernel.crio.capacity;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.ReflectionUtil;

/**
 * This class provide the minimal definition of a implementation of capacity.
 * 
 * @author $Author: ngaud$
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class CapacityImplementation {

	private final CapacityImplementationType type;
	private SoftReference<Collection<Class<? extends Capacity>>> implementedCapacities = null;

	
	/**
	 * Default constructor of capacity implementation with the default implementation type: DIRECT_ACTOMIC
	 */
	public CapacityImplementation() {
		this.type = CapacityImplementationType.DIRECT_ACTOMIC;
	}	
	
	/**
	 * @param type is the type of implementation.
	 */
	public CapacityImplementation(CapacityImplementationType type) {
		this.type = type;
	}
	
    /**
     * Replies the type of capacity implementation used by by this object.
     *
     * @return the type of capacity implementation.
     */
	public final CapacityImplementationType getImplementationType() {
		return this.type;
	}
	
	/** Replies the implemented capacities by this object.
	 * 
	 * @return the implemented capacities.
	 */
	public Collection<Class<? extends Capacity>> getCapacities() {
		Collection<Class<? extends Capacity>> collection = this.implementedCapacities==null ? null : this.implementedCapacities.get();
		if (collection==null) {
			collection = ReflectionUtil.getAllDirectInterfaces(getClass(), CapacityImplementation.class, Capacity.class);
			this.implementedCapacities = new SoftReference<Collection<Class<? extends Capacity>>>(collection);
		}
		return collection;
	}
	
	/**
     * Computes a result or makes a border effect, or throws an exception if unable to do so.
     *
     * @param call gives informations about capacity information and permits to replies results.
     * @throws Exception if unable to compute a result
     */
    public abstract void call(CapacityContext call) throws Exception;

	/** Send the given message to the logger as information message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#info(String)}.
	 * 
     * @param call gives informations about capacity invocation.
	 * @param message is the list of object to sent to logger.
	 * @see CapacityContext#getLogger()
	 * @see #debug(CapacityContext,Object...)
	 * @see #error(CapacityContext,Object...)
	 * @see #warning(CapacityContext,Object...)
	 * @LOGGINGAPI
	 */
	protected static final void print(CapacityContext call, Object... message) {
		Logger logger = call.getLogger();
		for(Object m : message) {
			if (m!=null) logger.info(m.toString());
		}
	}
	
	/** Send the given message to the logger as debugging message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#fine(String)}.
	 * 
     * @param call gives informations about capacity invocation.
	 * @param message is the list of object to sent to logger.
	 * @see CapacityContext#getLogger()
	 * @see #print(CapacityContext,Object...)
	 * @see #error(CapacityContext,Object...)
	 * @see #warning(CapacityContext,Object...)
	 * @LOGGINGAPI
	 */
	protected static final void debug(CapacityContext call, Object... message) {
		Logger logger = call.getLogger();
		for(Object m : message) {
			if (m!=null) logger.fine(m.toString());
		}
	}

	/** Send the given message to the logger as error message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#severe(String)}.
	 * 
     * @param call gives informations about capacity invocation.
	 * @param message is the list of object to sent to logger.
	 * @see CapacityContext#getLogger()
	 * @see #print(CapacityContext,Object...)
	 * @see #debug(CapacityContext,Object...)
	 * @see #warning(CapacityContext,Object...)
	 * @LOGGINGAPI
	 */
	protected static final void error(CapacityContext call, Object... message) {
		Logger logger = call.getLogger();
		for(Object m : message) {
			if (m!=null) logger.fine(m.toString());
		}
	}

	/** Send the given message to the logger as warning message.
	 * <p>
	 * Each of the given parameters is sent to {@link Logger#warning(String)}.
	 * 
     * @param call gives informations about capacity invocation.
	 * @param message is the list of object to sent to logger.
	 * @see CapacityContext#getLogger()
	 * @see #print(CapacityContext,Object...)
	 * @see #debug(CapacityContext,Object...)
	 * @see #error(CapacityContext,Object...)
	 * @LOGGINGAPI
	 */
	protected static final void warning(CapacityContext call, Object... message) {
		Logger logger = call.getLogger();
		for(Object m : message) {
			if (m!=null) logger.warning(m.toString());
		}
	}

}