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
package org.janusproject.kernel.probe;

import java.util.Set;

/** A probe permits to obtain information on agents.
 * <p>
 * A probe must not be invasive and the agent is
 * able to ignore them (ie. to not provide information).
 * <p>
 * This interface is strongly inspirated by the <code>Probe</code>
 * interface from the
 * <a href="http://www.arakhne.org/tinyMAS/">tinyMAS project</a>.
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Probe {

	/** Replies the names of the probed values inside the agent.
	 * 
	 * @return the list of names of the watched attributes. 
	 */
	public Set<String> getProbedValueNames();

	/** Replies if this probe contains a value.
	 * 
	 * @return <code>true</code> if an attribute is watchable, otherwise <code>false</code>. 
	 */
	public boolean hasProbeValues();

	/** Replies if the specified probe value exists.
	 *
	 * @param probeValueName is the name of watched value to test.
	 * @return <code>true</code> if an attribute is watchable, otherwise <code>false</code>.  
	 */
	public boolean hasProbeValue(String probeValueName);
	
	/** Release this probe.
	 * <p>
	 * This function permits to release several memory allocated by this probe.
	 * The probe is supposed to be no more used after a call to this method.
	 */
	public void releaseProbe();
	
	/** Replies if the probe is alive.
	 * A probe is alive when it is attached to an entity and
	 * the function {@link #releaseProbe()} was never called.
	 * When a probe is alive, it is possible to obtain the probed values.
	 * 
	 * @return <code>true</code> if the probe is alive; otherwise <code>false</code>.
	 * @since 0.5
	 */
	public boolean isAlive();

}