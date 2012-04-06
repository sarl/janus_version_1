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
package org.janusproject.kernel.schedule;

import java.util.logging.Logger;

import org.janusproject.kernel.logger.LoggerProvider;

/**
 * Define a execution policy among a set of activable elements.
 * <p>
 * Before invoking {@link #live()}, the Janus kernel is trying to synchronize the activator
 * to ensure that all the internal data structures are up-to-da	te.
 * 
 * @param <ET> if the type of the elementsd activated by this activator. 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Activator<ET extends Activable> extends Activable {

	/** Replies if the given type is activable by this activator.
	 * <p>
	 * To be consistent, this function may return <code>false</code> for any
	 * type which is not a subclass of <code>ET</code>. If the given
	 * <var>type</var> is a subclass of <code>ET</code>, this function could
	 * return <code>true</code> or <code>false</code> according to the
	 * internal activation rules.
	 * 
	 * @param type is the type to test.
	 * @return <code>true</code> if the <var>type</var> is supported by this activator,
	 * otherwise <code>false</code>.
	 */
	public boolean canActivate(Class<?> type);
	
	/** Replies if the activator contains something to activate.
	 * <p>
	 * The result of this function depends on the type of activator,
	 * see the documentation of each of them for more details.
	 * 
	 * @return <code>true</code> no object is activable, otherwise <code>false</code>.
	 */
	public boolean hasActivable();

	/** Replies if the activator has already contains at least one
	 * activable during its life.
	 * 
	 * @return <code>true</code> an activable object was never given
	 * to this activator before, otherwise <code>false</code>
	 */
	public boolean isUsed();

	/** Force this activator to be mark as used.
	 */
	public void used();

	/** Force this activator to be mark as unused.
	 */
	public void unused();

    /**
     * Returns an array containing all of the elements in this activator;
     * the runtime type of the returned array is that of the specified array.
     * If the activator fits in the specified array, it is returned therein.
     * Otherwise, the specified array is filled until its length is reach.
     *
     * <p>If this activator fits in the specified array with room to spare
     * (i.e., the array has more elements than this activator), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)
     *
     * @param a the array into which the elements of this activator are to be
     *        stored.
     * @return the count of elements copied in the given array.
     */
	public int toArray(ET[] a);
	
	/** Replies the count of activable objects inside this activator.
	 * 
	 * @return the count of activable objects inside this activator.
	 */
	public int size();
	
	/** Force this activator to synchronize itself.
	 * Any pending update of the internal structures must be applied
	 * during the invocation of this function.
	 * 
	 * @since 0.4
	 */
	public void sync();

	/** Replies the logger associated to this activator.
	 *
	 * @return the logger.
	 * @since 0.5
	 */
	public Logger getLogger();

	/** Set the provider that is able to provide
	 * to this activator a logger on demand.
	 *
	 * @param loggerProvider is the provider of logger to use.
	 * @since 0.5
	 */
	public void setLoggerProvider(LoggerProvider loggerProvider);

}
