/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2011 Janus Core Developers
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

import java.util.Collection;


/**
 * Define a container of activators. The container has its own execution resource
 * 
 * @param <A> is the type of supported activator.
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Scheduler<A extends Activator<?>>
extends Activator<A> {

	/** Add an activator.
	 * 
	 * @param activator is the activator to add inside this scheduler. 
	 */
	public void addActivator(A activator);
	
	/** Add all activators.
	 * 
	 * @param activators are the activators to add inside this scheduler. 
	 */
	public void addAllActivators(Collection<? extends A> activators);

	/** Remove an activator.
	 * 
	 * @param activator is the activator to remove from this scheduler. 
	 */
	public void removeActivator(A activator);

	/** Remove all activators.
	 */
	public void removeAllActivators();

	/** Remove all given activators.
	 * 
	 * @param activators are the activators to remove from this scheduler. 
	 */
	public void removeAllActivators(Collection<? extends A> activators);

}
